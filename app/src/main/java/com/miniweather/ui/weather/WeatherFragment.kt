package com.miniweather.ui.weather

import android.Manifest.permission
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.miniweather.R
import com.miniweather.databinding.FragmentWeatherBinding
import com.miniweather.model.Weather
import com.miniweather.service.network.ImageService
import com.miniweather.service.permissions.PermissionService
import com.miniweather.ui.base.BaseFragment
import com.miniweather.ui.base.injector
import javax.inject.Inject

class WeatherFragment :
    BaseFragment<WeatherContract.View, WeatherContract.Presenter, FragmentWeatherBinding>(),
    WeatherContract.View {

    @Inject
    lateinit var imageService: ImageService

    @Inject
    lateinit var permissionService: PermissionService

    @Inject
    override lateinit var presenter: WeatherContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionService.register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.weatherRefreshButton.setOnClickListener {
            presenter.onRefreshButtonClicked()
        }
    }

    override fun injectDependencies() {
        injector.inject(this)
    }

    override fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentWeatherBinding = FragmentWeatherBinding.inflate(
        inflater, container, false
    )

    override fun showWeather(weather: Weather) {
        hideLoading()

        binding.weatherCard.visibility = View.VISIBLE
        binding.weatherCard.showWeather(weather)
        binding.weatherCard.updateIcon(imageService, weather.iconUrl)
    }

    override fun showLastUpdatedInfo(location: String, time: String) {
        binding.weatherCard.showLastUpdatedInfo(
            getString(
                R.string.weather_last_updated,
                time,
                location
            )
        )
    }

    override suspend fun getLocationPermission(): Boolean =
        permissionService.request(requireContext(), permission.ACCESS_FINE_LOCATION)

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
