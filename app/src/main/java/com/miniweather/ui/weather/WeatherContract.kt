package com.miniweather.ui.weather

import com.miniweather.model.Weather
import com.miniweather.ui.base.BaseContract

interface WeatherContract : BaseContract {

    interface View : BaseContract.View {
        suspend fun getLocationPermission(): Boolean
        fun showLoading()
        fun showWeather(weather: Weather)
        fun showLastUpdatedInfo(location: String, time: String)
        fun showError(error: String)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun onRefreshButtonClicked()
    }
}
