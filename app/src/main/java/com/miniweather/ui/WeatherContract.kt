package com.miniweather.ui

import com.miniweather.model.Weather

interface WeatherContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun updateWeather(weather: Weather)
        fun showNetworkError()
        fun showCachedDataInfo(location: String, time: String)
        fun hasLocationPermission(): Boolean
        fun requestLocationPermission()
        fun showPermissionError()
    }

    interface Presenter {
        fun onStart(view: View)
        fun onStop()
        fun onRefreshButtonClicked()
        fun onLocationPermissionGranted()
        fun onLocationPermissionDenied()
    }
}
