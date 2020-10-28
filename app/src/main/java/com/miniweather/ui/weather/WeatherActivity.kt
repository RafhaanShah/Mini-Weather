package com.miniweather.ui.weather

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.miniweather.R
import com.miniweather.app.BaseApplication
import com.miniweather.databinding.ActivityWeatherBinding
import com.miniweather.model.Weather
import com.miniweather.service.network.ImageService
import com.miniweather.ui.base.BaseActivity
import javax.inject.Inject

class WeatherActivity : BaseActivity<WeatherContract.View, WeatherContract.Presenter>(), WeatherContract.View {

    companion object {
        const val PERMISSION_CODE_LOCATION = 100
    }

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
        (application as BaseApplication).getAppComponent().inject(this)
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

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestLocationPermission() {
        requestPermissions(
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_CODE_LOCATION
        )
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CODE_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                presenter.onLocationPermissionGranted()
            } else {
                presenter.onLocationPermissionDenied()
            }
        }
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
