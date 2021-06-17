package com.miniweather.ui.weather

import com.miniweather.mapper.ErrorMapper
import com.miniweather.mapper.ErrorType
import com.miniweather.model.Weather
import com.miniweather.provider.DateTimeProvider
import com.miniweather.repository.WeatherRepository
import com.miniweather.service.location.LocationService
import com.miniweather.ui.base.BasePresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class WeatherPresenter @Inject constructor(
    private val locationService: LocationService,
    private val dateTimeProvider: DateTimeProvider,
    private val weatherRepository: WeatherRepository,
    private val errorMapper: ErrorMapper,
    override val dispatcher: CoroutineDispatcher
) : BasePresenter<WeatherContract.View>(), WeatherContract.Presenter {

    override fun onAttachView(view: WeatherContract.View) {
        super.onAttachView(view)
        checkLocationPermission()
    }

    override fun onRefreshButtonClicked() {
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        launch {
            view.let { view ->
                if (view.getLocationPermission()) {
                    getWeather()
                } else {
                    view.showError(
                        errorMapper.mapError(ErrorType.LOCATION_PERMISSION)
                    )
                }
            }
        }
    }

    private suspend fun getWeather() {
        view.showLoading()
        try {
            weatherRepository.getWeather(locationService.getLocation())
                .onSuccess { showWeather(it) }
                .onFailure {
                    view.showError(
                        errorMapper.mapNetworkException(
                            it
                        )
                    )
                }
        } catch (e: TimeoutCancellationException) {
            view.showError(errorMapper.mapError(ErrorType.LOCATION_TIMEOUT))
        }
    }

    private fun showWeather(weather: Weather) {
        view.showWeather(weather)
        val recentTime = dateTimeProvider.getCurrentTime() - TimeUnit.MINUTES.toMillis(5)
        if (weather.timestamp < recentTime) {
            view.showLastUpdatedInfo(
                weather.location,
                dateTimeProvider.getRelativeTimeString(weather.timestamp)
            )
        }
    }
}
