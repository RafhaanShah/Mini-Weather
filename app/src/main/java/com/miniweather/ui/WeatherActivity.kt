package com.miniweather.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.miniweather.BaseDaggerApplication
import com.miniweather.R
import com.miniweather.databinding.ActivityWeatherBinding
import com.miniweather.model.Weather
import com.miniweather.service.ImageService
import kotlinx.android.synthetic.main.view_weather.view.*
import javax.inject.Inject

class WeatherActivity : AppCompatActivity(), WeatherContract.View {

    private val permissionCodeLocation = 100

    private lateinit var binding: ActivityWeatherBinding

    @Inject
    lateinit var imageService: ImageService

    @Inject
    lateinit var presenter: WeatherContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseDaggerApplication).getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.onStart(this)

        binding.weatherFab.setOnClickListener {
            presenter.onRefreshButtonClicked()
        }
    }

    override fun onDestroy() {
        presenter.onStop()
        super.onDestroy()
    }

    override fun updateWeather(weather: Weather) {
        binding.weatherCard.weatherCardLayout.visibility = View.VISIBLE
        binding.weatherCard.weatherConditionText.text = weather.condition
        binding.weatherCard.weatherTemperatureText.text =
            getString(R.string.weather_temperature_text, weather.temperature)
        binding.weatherCard.weatherWindSpeedText.text =
            getString(R.string.weather_wind_speed_text, weather.windSpeed)
        binding.weatherCard.weatherWindDirectionText.text = weather.windDirection
        binding.weatherCard.weatherLastUpdatedText.visibility = View.GONE
        binding.weatherProgress.visibility = View.GONE

        imageService.loadImage(binding.weatherLayout.weather_icon, weather.iconUrl)
    }

    override fun showCachedDataInfo(location: String, time: String) {
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
            permissionCodeLocation
        )
    }

    override fun showLoading() {
        binding.weatherProgress.visibility = View.VISIBLE
        binding.weatherCard.weatherCardLayout.visibility = View.GONE
        binding.weatherErrorMessageCard.visibility = View.GONE
    }

    override fun hideLoading() {
        binding.weatherProgress.visibility = View.GONE
    }

    override fun showNetworkError() {
        binding.weatherErrorMessageText.text = getString(R.string.weather_error_offline)
        showErrorCard()
    }

    override fun showPermissionError() {
        binding.weatherErrorMessageText.text = getString(R.string.weather_error_permission)
        showErrorCard()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionCodeLocation) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                presenter.onLocationPermissionGranted()
            } else {
                presenter.onLocationPermissionDenied()
            }
            return

        }
    }

    private fun showErrorCard() {
        binding.weatherErrorMessageCard.visibility = View.VISIBLE
        binding.weatherProgress.visibility = View.GONE
        binding.weatherCard.weatherCardLayout.visibility = View.GONE
    }
}
