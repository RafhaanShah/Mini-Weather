package com.miniweather.model

import com.google.common.truth.Truth.assertThat
import com.miniweather.di.NetworkModule
import com.miniweather.testutil.fakeWeatherResponse
import com.miniweather.testutil.readTestResourceFile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test

class WeatherSerializationTest {

    private val jsonFormat: Json = NetworkModule.provideJsonSerializer()

    @Test
    fun whenDeserializing_givesCorrectOutput() {
        val json = readTestResourceFile("responses/weather.json")

        val actual = jsonFormat.decodeFromString<WeatherResponse>(json)

        assertThat(actual).isEqualTo(fakeWeatherResponse)
    }

}
