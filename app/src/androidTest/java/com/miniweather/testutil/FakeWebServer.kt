package com.miniweather.testutil

import com.miniweather.BuildConfig.API_BASE_URL
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.util.*
import java.util.concurrent.TimeUnit


class FakeWebServer {

    private val port = 9999
    private val server = MockWebServer()
    private val requests: Queue<MockRequest> = ArrayDeque()

    fun start() {
        server.start(port)
        if (server.url("/").toString() != API_BASE_URL) {
            throw Exception(
                "Failed to start mock server on correct address: got ${server.url("/")} but wanted " + API_BASE_URL
            )
        }
    }

    fun shutdown() {
        server.shutdown()
    }

    fun verifyRequests() {
        while (requests.isNotEmpty()) {
            val expected = requests.remove()
            val actual = server.takeRequest(100, TimeUnit.MILLISECONDS)
                ?: throw Exception("Did not receive expected request ${expected.method} ${expected.path}")

            if ((expected.method != actual.method) || (actual.path?.startsWith(expected.path) == false)) {
                throw Exception(
                    "Expected request ${expected.method} ${expected.path} " +
                            "did not match actual ${actual.method} ${actual.path}"
                )
            }
        }
    }

    fun expectRequest(body: String, path: String, method: String = "GET", code: Int = 200) {
        server.enqueue(MockResponse().setResponseCode(code).setBody(body))
        requests.add(MockRequest("/$path", method))
    }

}

data class MockRequest(val path: String, val method: String)
