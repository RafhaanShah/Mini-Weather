package com.miniweather.testutil

import android.util.Log
import com.miniweather.util.Empty
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.junit.rules.ExternalResource
import java.io.IOException
import java.util.ArrayDeque
import java.util.Queue

class MockWebServerRule(
    private val dispatcherMode: DispatcherMode = DispatcherMode.MATCH
) : ExternalResource() {

    private val tag = "MockWebServerRule"
    private val server: MockWebServer = MockWebServer()

    override fun before() {
        server.dispatcher = getDispatcher(dispatcherMode)
        server.start()
    }

    override fun after() {
        try {
            server.shutdown()
        } catch (e: IOException) {
            Log.e(tag, "MockWebServer failed to shutdown: ", e)
        }
    }

    fun getPort(): Int = server.port

    fun expectQueuedRequest(mockResponse: MockResponse) {
        check(dispatcherMode == DispatcherMode.QUEUE) { "Invalid dispatcher $dispatcherMode" }
        server.enqueue(mockResponse)
    }

    fun expectMatchingRequest(
        mockRequest: MockRequest,
        mockResponse: MockResponse
    ) {
        check(dispatcherMode == DispatcherMode.MATCH) { "Invalid dispatcher $dispatcherMode" }
        server.enqueue(mockRequest, mockResponse)
    }

    private fun MockWebServer.enqueue(request: MockRequest, response: MockResponse) {
        (dispatcher as MatchingDispatcher).enqueueResponse(request, response.clone())
    }

    private fun getDispatcher(dispatcherMode: DispatcherMode): Dispatcher =
        when (dispatcherMode) {
            DispatcherMode.QUEUE -> QueueDispatcher()
            DispatcherMode.MATCH -> MatchingDispatcher()
        }

    enum class DispatcherMode {
        QUEUE,
        MATCH
    }
}

class MatchingDispatcher : Dispatcher() {

    private val tag = "MatchingDispatcher"
    private val responses: MutableMap<MockRequest, Queue<MockResponse>> = mutableMapOf()

    override fun dispatch(request: RecordedRequest): MockResponse {
        val key = responses.keys.firstOrNull {
            it.matches(request)
        }

        val responseQueue = responses[key] ?: return notFound(request.requestLine)
        return responseQueue.poll() ?: return notFound(request.requestLine)
    }

    fun enqueueResponse(request: MockRequest, response: MockResponse) =
        responses.getOrPut(request, { ArrayDeque() }).add(response)

    private fun notFound(requestLine: String): MockResponse {
        Log.w(tag, "No MockWebServer response for: $requestLine")
        return MockResponse().setResponseCode(404)
    }

    private fun MockRequest.matches(request: RecordedRequest): Boolean {
        if (method != request.method)
            return false

        val requestUrl = requireNotNull(request.requestUrl) { "Request URL cannot be null" }
        if ("/$path" != requestUrl.toUrl().path)
            return false

        return true
    }
}

data class MockRequest(
    val path: String = String.Empty,
    val method: String = "GET",
)
