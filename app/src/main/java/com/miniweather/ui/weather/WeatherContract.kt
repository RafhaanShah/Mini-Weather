package com.miniweather.ui.weather

import com.miniweather.model.Weather

interface WeatherContract {

    interface View {
        fun hasLocationPermission(): Boolean
        fun requestLocationPermission()
        fun showLoading()
        fun showWeather(weather: Weather)
        fun showLastUpdatedInfo(location: String, time: String)
        fun showNetworkError()
        fun showPermissionError()
        fun showLocationError()
    }

    interface Presenter {
        fun onStart(view: View)
        fun onStop()
        fun onRefreshButtonClicked()
        fun onLocationPermissionGranted()
        fun onLocationPermissionDenied()
    }

}
