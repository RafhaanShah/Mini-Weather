package com.miniweather.ui.weather

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import com.miniweather.R
import com.miniweather.databinding.ComponentWeatherCardBinding
import com.miniweather.model.Weather
import com.miniweather.service.network.ImageService

class WeatherCardComponent(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    private val binding: ComponentWeatherCardBinding =
        ComponentWeatherCardBinding.inflate(LayoutInflater.from(context), this, true)

    fun showWeather(weather: Weather) {
        binding.weatherConditionText.text = weather.condition
        binding.weatherTemperatureText.text =
            resources.getString(R.string.weather_temperature_text, weather.temperature)
        binding.weatherWindSpeedText.text =
            resources.getString(R.string.weather_wind_speed_text, weather.windSpeed)
        binding.weatherWindDirectionText.text = weather.windDirection
        binding.weatherLastUpdatedText.visibility = View.GONE
    }

    fun showLastUpdatedInfo(info: String) {
        binding.weatherLastUpdatedText.visibility = View.VISIBLE
        binding.weatherLastUpdatedText.text = info
    }

    fun updateIcon(imageService: ImageService, url: String) {
        imageService.loadImage(binding.weatherIcon, url)
    }
}
