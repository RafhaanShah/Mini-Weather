package com.miniweather.model

import com.miniweather.di.NetworkModule
import com.miniweather.testutil.BaseTest
import com.miniweather.testutil.fakeWeatherResponse
import com.miniweather.testutil.readTestResourceFile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherSerializationTest : BaseTest() {

    private val jsonFormat: Json = NetworkModule.provideJsonSerializer()

    @Test
    fun whenDeserializing_givesCorrectOutput() {
        val json = readTestResourceFile("responses/weather.json")

        val actual = jsonFormat.decodeFromString<WeatherResponse>(json)

        assertEquals(fakeWeatherResponse, actual)
    }

}
