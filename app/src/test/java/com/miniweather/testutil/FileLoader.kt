package com.miniweather.testutil

import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

fun BaseTest.readTestResourceFile(filename: String) =
    InputStreamReader(
        javaClass.getResourceAsStream("/$filename"),
        StandardCharsets.UTF_8
    ).buffered().readText()
