package com.miniweather.ui.weather

import com.miniweather.model.Weather
import com.miniweather.ui.base.BaseContract

interface WeatherContract : BaseContract {

    interface View : BaseContract.View {
        fun hasLocationPermission(): Boolean
        fun requestLocationPermission()
        fun showLoading()
        fun showWeather(weather: Weather)
        fun showLastUpdatedInfo(location: String, time: String)
        fun showNetworkError()
        fun showPermissionError()
        fun showLocationError()
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun onRefreshButtonClicked()
        fun onLocationPermissionGranted()
        fun onLocationPermissionDenied()
    }

}
