package com.miniweather.pages

import com.miniweather.R.id.*
import com.miniweather.model.Weather
import com.miniweather.testutil.BasePage

class WeatherPage : BasePage() {

    init {
        shouldBeDisplayed()
    }

    override fun shouldBeDisplayed() {
        shouldBeVisible(weather_activity_layout)
    }

    fun shouldShowWeather(fakeWeather: Weather) {
        shouldBeVisible(weather_card)

        shouldHaveText(weather_condition_text, fakeWeather.condition)
        shouldHaveText(weather_temperature_text, fakeWeather.temperature.toString())
        shouldHaveText(weather_wind_speed_text, fakeWeather.windSpeed.toString())
        shouldHaveText(weather_wind_direction_text, fakeWeather.windDirection)

        shouldNotBeVisible(weather_progress)
        shouldNotBeVisible(weather_last_updated_text)
        shouldNotBeVisible(weather_error_message_card)
    }

    fun shouldShowLastUpdated(time: String, location: String) {
        shouldBeVisible(weather_last_updated_text)
        shouldHaveText(weather_last_updated_text, location)
        shouldHaveText(weather_last_updated_text, time)
    }

    fun shouldShowLoading() {
        shouldBeVisible(weather_progress)
        shouldNotBeVisible(weather_card)
        shouldNotBeVisible(weather_error_message_card)
        shouldNotBeVisible(weather_refresh_button)
    }

    fun shouldShowErrorMessage(message: String) {
        shouldBeVisible(weather_error_message_card)
        shouldHaveText(weather_error_message_text, message)

        shouldNotBeVisible(weather_progress)
        shouldNotBeVisible(weather_card)
    }

    fun pressRefresh() {
        performClick(weather_refresh_button)
    }

}
