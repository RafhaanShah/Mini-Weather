package com.miniweather.ui

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
        const val PREF_CONDITION = "condition"
        const val PREF_TEMP = "temp"
        const val PREF_WIND_SPEED = "wind_speed"
        const val PREF_WIND_DIRECTION = "wind_direction"
        const val PREF_LOCATION = "location"
        const val PREF_UPDATE_TIME = "update_time"
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
        weatherService.getWeather(lat, lon) { weather, success ->
            view?.hideLoading()
            if (success) {
                weather?.let {
                    view?.updateWeather(it)
                    cacheWeather(it)
                }
            } else {
                showCachedDataIfAvailable()
            }
        }
    }

    private fun showCachedDataIfAvailable() {
        val cachedTime = sharedPreferenceService.getLong(PREF_UPDATE_TIME)

        if (timeService.timeDifferenceInHours(cachedTime) < 24) {
            val weather = getCachedWeather()
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
        sharedPreferenceService.saveString(PREF_CONDITION, weather.condition)
        sharedPreferenceService.saveInt(PREF_TEMP, weather.temperature)
        sharedPreferenceService.saveInt(PREF_WIND_SPEED, weather.windSpeed)
        sharedPreferenceService.saveString(PREF_WIND_DIRECTION, weather.windDirection)
        sharedPreferenceService.saveString(PREF_LOCATION, weather.location)
        sharedPreferenceService.saveLong(PREF_UPDATE_TIME, timeService.getCurrentTime())
    }

    private fun getCachedWeather(): Weather {
        return Weather(
            sharedPreferenceService.getString(PREF_CONDITION),
            sharedPreferenceService.getInt(PREF_TEMP),
            sharedPreferenceService.getInt(PREF_WIND_SPEED),
            sharedPreferenceService.getString(PREF_WIND_DIRECTION),
            sharedPreferenceService.getString(PREF_LOCATION),
            "",
        )
    }
}
