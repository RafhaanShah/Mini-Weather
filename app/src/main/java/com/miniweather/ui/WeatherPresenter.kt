package com.miniweather.ui

import com.miniweather.model.DataResult
import com.miniweather.service.location.LocationService
import com.miniweather.service.util.TimeService
import com.miniweather.service.weather.WeatherService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WeatherPresenter @Inject constructor(
    private val locationService: LocationService,
    private val timeService: TimeService,
    private val weatherService: WeatherService,
    dispatcher: CoroutineDispatcher
) : WeatherContract.Presenter {

    private val job = Job()
    private val scope = CoroutineScope(job + dispatcher)
    private var view: WeatherContract.View? = null

    override fun onStart(view: WeatherContract.View) {
        this.view = view

        if (checkPermission()) {
            getLocation()
        }
    }

    override fun onStop() {
        job.cancel()
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
        scope.launch {
            when (val result = weatherService.getWeather(lat, lon)) {
                is DataResult.Success -> {
                    val weather = result.data
                    view?.updateWeather(weather)
                    if (weather.timestamp < (timeService.getCurrentTime() - TimeUnit.MINUTES.toMillis(5))) {
                        view?.showCachedDataInfo(weather.location, timeService.getRelativeTimeString(weather.timestamp))
                    }
                }
                is DataResult.Failure -> {
                    view?.showNetworkError()
                }
            }
        }
    }

}
