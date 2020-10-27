package com.miniweather.ui.weather

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.miniweather.R
import com.miniweather.app.BaseDaggerApplication
import com.miniweather.databinding.ActivityWeatherBinding
import com.miniweather.model.Weather
import com.miniweather.service.network.ImageService
import kotlinx.android.synthetic.main.view_weather.view.*
import javax.inject.Inject

class WeatherActivity : AppCompatActivity(), WeatherContract.View {

    companion object {
        const val PERMISSION_CODE_LOCATION = 100
    }

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

        imageService.loadImage(this, binding.weatherLayout.weather_icon, weather.iconUrl)
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
