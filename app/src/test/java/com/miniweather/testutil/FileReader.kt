package com.miniweather.testutil

fun <T : Any> T.readTestResourceFile(filename: String): String =
    javaClass.getResourceAsStream("/$filename")!!.bufferedReader().readText()
