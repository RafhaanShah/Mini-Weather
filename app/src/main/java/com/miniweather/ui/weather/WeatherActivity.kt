package com.miniweather.ui.weather

import android.Manifest.permission
import android.os.Bundle
import android.view.View
import com.miniweather.R
import com.miniweather.databinding.ActivityWeatherBinding
import com.miniweather.model.Weather
import com.miniweather.service.network.ImageService
import com.miniweather.ui.base.BaseActivity
import com.miniweather.ui.base.injector
import javax.inject.Inject

class WeatherActivity : BaseActivity<WeatherContract.View, WeatherContract.Presenter>(), WeatherContract.View {

    @Inject
    lateinit var imageService: ImageService

    @Inject
    override lateinit var presenter: WeatherContract.Presenter

    override lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.weatherFab.setOnClickListener {
            presenter.onRefreshButtonClicked()
        }
    }

    override fun injectDependencies() {
        injector.inject(this)
    }

    override fun bindView() {
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun showWeather(weather: Weather) {
        hideLoading()

        binding.weatherCard.weatherCardLayout.visibility = View.VISIBLE
        binding.weatherCard.weatherConditionText.text = weather.condition
        binding.weatherCard.weatherTemperatureText.text =
            getString(R.string.weather_temperature_text, weather.temperature)
        binding.weatherCard.weatherWindSpeedText.text =
            getString(R.string.weather_wind_speed_text, weather.windSpeed)
        binding.weatherCard.weatherWindDirectionText.text = weather.windDirection
        binding.weatherCard.weatherLastUpdatedText.visibility = View.GONE

        imageService.loadImage(this, binding.weatherCard.weatherIcon, weather.iconUrl)
    }

    override fun showLastUpdatedInfo(location: String, time: String) {
        binding.weatherCard.weatherLastUpdatedText.visibility = View.VISIBLE
        binding.weatherCard.weatherLastUpdatedText.text =
            getString(R.string.weather_last_updated, time, location)
    }

    override suspend fun requestLocationPermission(): Boolean {
        return checkAndRequestPermission(permission.ACCESS_FINE_LOCATION)
    }

    override fun showLoading() {
        binding.weatherProgress.visibility = View.VISIBLE
        binding.weatherCard.weatherCardLayout.visibility = View.GONE
        binding.weatherErrorMessageCard.visibility = View.GONE
        binding.weatherFab.visibility = View.GONE
    }

    override fun showNetworkError() {
        binding.weatherErrorMessageText.text = getString(R.string.error_network_request)
        showErrorCard()
    }

    override fun showPermissionError() {
        binding.weatherErrorMessageText.text = getString(R.string.error_permission_location)
        showErrorCard()
    }

    override fun showLocationError() {
        binding.weatherErrorMessageText.text = getString(R.string.error_location_timeout)
        showErrorCard()
    }

    private fun showErrorCard() {
        hideLoading()
        binding.weatherErrorMessageCard.visibility = View.VISIBLE
        binding.weatherCard.weatherCardLayout.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.weatherProgress.visibility = View.GONE
        binding.weatherFab.visibility = View.VISIBLE
    }

}
