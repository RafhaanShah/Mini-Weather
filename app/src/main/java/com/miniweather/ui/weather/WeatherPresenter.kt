package com.miniweather.ui.weather

import com.miniweather.R
import com.miniweather.model.Weather
import com.miniweather.service.location.LocationService
import com.miniweather.service.util.StringResourceService
import com.miniweather.service.util.TimeService
import com.miniweather.service.weather.WeatherService
import com.miniweather.ui.base.BasePresenter
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class WeatherPresenter @Inject constructor(
    private val locationService: LocationService,
    private val timeService: TimeService,
    private val weatherService: WeatherService,
    private val stringResourceService: StringResourceService,
    @Named("Main") dispatcher: CoroutineDispatcher
) : BasePresenter<WeatherContract.View>(), WeatherContract.Presenter {

    private val job = Job()
    private val scope = CoroutineScope(job + dispatcher)

    override fun onAttachView(view: WeatherContract.View) {
        super.onAttachView(view)
        checkLocationPermission()
    }

    override fun onDetachView() {
        job.cancel()
        super.onDetachView()
    }

    override fun onRefreshButtonClicked() {
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        scope.launch {
            view?.let { view ->
                if (view.getLocationPermission()) {
                    getWeather()
                } else {
                    view.showError(stringResourceService.getString(R.string.error_permission_location))
                }
            }
        }
    }

    private suspend fun getWeather() {
        view?.showLoading()
        try {
            weatherService.getWeather(locationService.getLocation())
                .onSuccess { showWeather(it) }
                .onFailure {
                    val errorMessage = when (it) {
                        is HttpException -> stringResourceService.getString(R.string.error_network_response)
                        else -> stringResourceService.getString(R.string.error_network_request)
                    }
                    view?.showError(errorMessage)
                }
        } catch (e: TimeoutCancellationException) {
            view?.showError(stringResourceService.getString(R.string.error_location_timeout))
        }
    }

    private fun showWeather(weather: Weather) {
        view?.showWeather(weather)
        if (weather.timestamp < (timeService.getCurrentTime() - TimeUnit.MINUTES.toMillis(5))) {
            view?.showLastUpdatedInfo(
                weather.location,
                timeService.getRelativeTimeString(weather.timestamp)
            )
        }
    }

}
