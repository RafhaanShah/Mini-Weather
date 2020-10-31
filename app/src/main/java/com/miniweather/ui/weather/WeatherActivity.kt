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

        binding.weatherRefreshButton.setOnClickListener {
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

        binding.weatherCard.visibility = View.VISIBLE
        binding.weatherCard.showWeather(weather)
        binding.weatherCard.updateIcon(imageService, weather.iconUrl)
    }

    override fun showLastUpdatedInfo(location: String, time: String) {
        binding.weatherCard.showLastUpdatedInfo(getString(R.string.weather_last_updated, time, location))
    }

    override suspend fun requestLocationPermission(): Boolean {
        return checkAndRequestPermission(permission.ACCESS_FINE_LOCATION)
    }

    override fun showLoading() {
        binding.weatherProgress.visibility = View.VISIBLE
        binding.weatherCard.visibility = View.GONE
        binding.weatherErrorMessageCard.visibility = View.GONE
        binding.weatherRefreshButton.visibility = View.GONE
    }

    override fun showError(error: String) {
        binding.weatherErrorMessageText.text = error
        showErrorCard()
    }

    private fun showErrorCard() {
        hideLoading()
        binding.weatherErrorMessageCard.visibility = View.VISIBLE
        binding.weatherCard.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.weatherProgress.visibility = View.GONE
        binding.weatherRefreshButton.visibility = View.VISIBLE
    }

}
