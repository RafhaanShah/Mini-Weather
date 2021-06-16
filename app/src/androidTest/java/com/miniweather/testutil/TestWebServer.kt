package com.miniweather.testutil

import com.miniweather.util.Empty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.util.*
import java.util.concurrent.TimeUnit


class TestWebServer {

    private var requests: Queue<MockRequest> = ArrayDeque()
    private var server: MockWebServer = MockWebServer()

    fun start() {
        server.start()
    }

    fun stop() {
        server.shutdown()
        verifyRequests()
    }

    fun getPort(): Int = server.port

    fun expectRequest(
        mockRequest: MockRequest,
        mockResponse: MockResponse
    ) {
        requests.add(mockRequest)
        server.enqueue(mockResponse)
    }

    private fun verifyRequests() {
        while (requests.isNotEmpty()) {
            val expected = requests.remove()
            val actual = server.takeRequest(100, TimeUnit.MILLISECONDS)
                ?: throw Exception(
                    "Did not receive expected request: " +
                            " ${expected.method} ${expected.path}"
                )

            // path does not have a leading slash
            println(actual.path)
            if ((expected.method != actual.method)
                || (actual.path?.startsWith("/${expected.path}") == false)
            ) {
                throw Exception(
                    "Expected request ${expected.method} ${expected.path} " +
                            "did not match actual ${actual.method} ${actual.path}"
                )
            }

            for (header in expected.headers) {
                val actualHeaders = actual.headers
                if (actualHeaders[header.key] != header.value) {
                    throw Exception(
                        "Expected header ${header.key}:${header.value} " +
                                "did not match actual ${actualHeaders[header.key]}"
                    )
                }
            }

            // path includes query params
            for (queryParam in expected.queryParams) {
                val path = actual.path
                if (path?.contains("${queryParam.key}=${queryParam.value}") == false) {
                    throw Exception(
                        "Expected query parameter ${queryParam.key}=${queryParam.value} " +
                                "was not found in: $path"
                    )
                }
            }

            expected.requestBodyVerifier?.validate(actual.body.readUtf8())
        }
    }

}

interface MockRequestBodyVerifier {
    fun validate(body: String)
}

interface JsonRequestBodyVerifier<T : Any> : MockRequestBodyVerifier {
    override fun validate(body: String) {
        val obj = Json.decodeFromString(getSerializer(), body)
        validateJson(obj)
    }

    fun getSerializer(): KSerializer<T>

    fun validateJson(obj: T)
}

data class TestHttpCall(
    val mockRequest: MockRequest = MockRequest(),
    val mockResponse: MockResponse = MockResponse().setResponseCode(200),
    val responseFile: String? = null
)

data class MockRequest(
    val path: String = String.Empty,
    val method: String = "GET",
    val headers: Map<String, String> = emptyMap(),
    val queryParams: Map<String, String> = emptyMap(),
    val requestBodyVerifier: MockRequestBodyVerifier? = null
)
