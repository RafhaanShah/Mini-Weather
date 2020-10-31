package com.miniweather.testutil

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer


class FakeWebServer {

    private val port = 9999
    private val server = MockWebServer()

    fun start() {
        server.start(port)
    }

    fun shutdown() {
        server.shutdown()
    }

    fun expectRequest(code: Int = 200, body: String = "") {
        server.enqueue(MockResponse().setResponseCode(code).setBody(body))
    }

}
