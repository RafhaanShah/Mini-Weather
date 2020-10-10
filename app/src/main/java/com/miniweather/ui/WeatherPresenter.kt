package com.miniweather.ui

import com.miniweather.model.DataResult
import com.miniweather.model.Weather
import com.miniweather.service.LocationService
import com.miniweather.service.SharedPreferenceService
import com.miniweather.service.TimeService
import com.miniweather.service.WeatherService
import javax.inject.Inject

class WeatherPresenter @Inject constructor(
    private val locationService: LocationService,
    private val weatherService: WeatherService,
    private val sharedPreferenceService: SharedPreferenceService,
    private val timeService: TimeService
) : WeatherContract.Presenter {

    companion object {
        const val PREF_CACHE = "weather_cache"
        const val PREF_UPDATE_TIME = "weather_update_time"
    }

    private var view: WeatherContract.View? = null

    override fun onStart(view: WeatherContract.View) {
        this.view = view

        if (checkPermission()) {
            getLocation()
        }
    }

    override fun onStop() {
        view = null
    }

    override fun onRefreshButtonClicked() {
        if (checkPermission()) {
            getLocation()
        }
    }

    override fun onLocationPermissionGranted() {
        getLocation()
    }

    override fun onLocationPermissionDenied() {
        view?.hideLoading()
        view?.showPermissionError()
    }

    private fun checkPermission(): Boolean {
        return if (view?.hasLocationPermission() == true) {
            true
        } else {
            view?.requestLocationPermission()
            false
        }
    }

    private fun getLocation() {
        view?.showLoading()
        locationService.getLocation { lat, lon ->
            getWeather(lat, lon)
        }
    }

    private fun getWeather(lat: Double, lon: Double) {
        weatherService.getWeather(lat, lon) {
            when (it) {
                is DataResult.Success -> {
                    val weather = it.data
                    view?.updateWeather(weather)
                    cacheWeather(weather)
                }
                is DataResult.Failure -> {
                    showCachedDataIfAvailable()
                }
            }
        }
    }

    private fun showCachedDataIfAvailable() {
        val cachedTime = sharedPreferenceService.getLong(PREF_UPDATE_TIME)

        if (timeService.timeDifferenceInHours(cachedTime) < 24 && sharedPreferenceService.hasSavedValue(PREF_CACHE)) {
            val weather: Weather = sharedPreferenceService.getWeather(PREF_CACHE)
            view?.updateWeather(weather)
            view?.showCachedDataInfo(
                weather.location,
                timeService.getRelativeTime(cachedTime)
            )
        } else {
            view?.showNetworkError()
        }
    }

    private fun cacheWeather(weather: Weather) {
        sharedPreferenceService.saveLong(PREF_UPDATE_TIME, timeService.getCurrentTime())
        sharedPreferenceService.saveWeather(PREF_CACHE, weather)
    }

}
