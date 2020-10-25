package com.miniweather.ui

import com.miniweather.model.DataResult
import com.miniweather.model.Location
import com.miniweather.model.Weather
import com.miniweather.service.location.LocationService
import com.miniweather.service.util.TimeService
import com.miniweather.service.weather.WeatherService
import kotlinx.coroutines.*
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
            getWeather()
        }
    }

    override fun onStop() {
        job.cancel()
        view = null
    }

    override fun onRefreshButtonClicked() {
        if (checkPermission()) {
            getWeather()
        }
    }

    override fun onLocationPermissionGranted() {
        getWeather()
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

    private fun getWeather() {
        view?.showLoading()
        scope.launch {
            try {
                when (val weatherResult = weatherService.getWeather(getLocation())) {
                    is DataResult.Success -> showWeather(weatherResult.data)
                    is DataResult.Failure -> view?.showNetworkError()
                }
            } catch (e: TimeoutCancellationException) {
                view?.showLocationError()
            }
        }
    }

    @Throws(TimeoutCancellationException::class)
    suspend fun getLocation(): Location {
        return withTimeout(TimeUnit.MINUTES.toMillis(1)) {
            locationService.getLocation()
        }
    }

    private fun showWeather(weather: Weather) {
        view?.showWeather(weather)
        if (weather.timestamp < (timeService.getCurrentTime() - TimeUnit.MINUTES.toMillis(5))) {
            view?.showLastUpdatedInfo(weather.location, timeService.getRelativeTimeString(weather.timestamp))
        }
    }

}
