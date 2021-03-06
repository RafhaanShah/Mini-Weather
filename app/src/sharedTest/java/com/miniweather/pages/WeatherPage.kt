package com.miniweather.pages

import com.miniweather.R.id.weather_activity_layout
import com.miniweather.R.id.weather_card
import com.miniweather.R.id.weather_condition_text
import com.miniweather.R.id.weather_error_message_card
import com.miniweather.R.id.weather_error_message_text
import com.miniweather.R.id.weather_last_updated_text
import com.miniweather.R.id.weather_progress
import com.miniweather.R.id.weather_refresh_button
import com.miniweather.R.id.weather_temperature_text
import com.miniweather.R.id.weather_wind_direction_text
import com.miniweather.R.id.weather_wind_speed_text
import com.miniweather.model.Weather
import com.miniweather.testutil.BasePage

class WeatherPage : BasePage(weather_activity_layout) {

    fun shouldShowWeather(fakeWeather: Weather, cached: Boolean = false) {
        shouldBeVisible(weather_card)

        shouldHaveText(weather_condition_text, fakeWeather.condition)
        shouldHaveText(weather_temperature_text, fakeWeather.temperature.toString())
        shouldHaveText(weather_wind_speed_text, fakeWeather.windSpeed.toString())
        shouldHaveText(weather_wind_direction_text, fakeWeather.windDirection)

        if (!cached)
            shouldNotBeVisible(weather_last_updated_text)

        shouldNotBeVisible(weather_progress)
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
