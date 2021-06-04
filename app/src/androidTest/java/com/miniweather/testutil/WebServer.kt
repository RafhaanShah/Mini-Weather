package com.miniweather.testutil

import android.content.res.AssetManager
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit


object WebServer {

    private lateinit var requests: Queue<MockRequest>
    private lateinit var assets: AssetManager
    private lateinit var server: MockWebServer

    fun start(assetManager: AssetManager) {
        assets = assetManager
        requests = ArrayDeque()
        server = MockWebServer()
        server.start()
    }

    fun shutdown() = server.shutdown()

    fun getPort(): Int = server.port

    fun verifyRequests() {
        while (requests.isNotEmpty()) {
            val expected = requests.remove()
            val actual = server.takeRequest(100, TimeUnit.MILLISECONDS)
                ?: throw Exception(
                    "Did not receive expected request: " +
                            " ${expected.method} ${expected.path}"
                )

            if ((expected.method != actual.method)
                || (actual.path?.startsWith(expected.path) == false)
            ) {
                throw Exception(
                    "Expected request ${expected.method} ${expected.path} " +
                            "did not match actual ${actual.method} ${actual.path}"
                )
            }
        }
    }

    fun expectRequest(
        path: String,
        method: String,
        code: Int,
        fileName: String?
    ) {
        server.enqueue(MockResponse().setResponseCode(code).apply {
            fileName?.let { file ->
                this.setBody(readTestResourceFile(file))
            }
        })
        requests.add(MockRequest("/$path", method))
    }

    private fun readTestResourceFile(fileName: String) =
        InputStreamReader(
            assets.open("responses/$fileName"),
            StandardCharsets.UTF_8
        ).buffered().readText()

}

data class MockRequest(val path: String, val method: String)
