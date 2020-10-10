package com.miniweather.ui

import com.miniweather.model.DataResult
import com.miniweather.model.Weather
import com.miniweather.service.LocationService
import com.miniweather.service.SharedPreferenceService
import com.miniweather.service.TimeService
import com.miniweather.service.WeatherService
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WeatherPresenterTest {

    @Mock
    private lateinit var mockLocationService: LocationService

    @Mock
    private lateinit var mockWeatherService: WeatherService

    @Mock
    private lateinit var mockSharedPreferenceService: SharedPreferenceService

    @Mock
    private lateinit var mockTimeService: TimeService

    @Mock
    private lateinit var mockView: WeatherContract.View

    private lateinit var presenter: WeatherPresenter

    private val fakeTime: Long = 129034871023487
    private val fakeLat = 1.1
    private val fakeLon = 2.2
    private val fakeWeather =
        Weather(
            "Sunny",
            69,
            42, "North",
            "London, UK",
            "https://weather.icon/0"
        )

    @Before
    fun setup() {
        presenter = WeatherPresenter(
            mockLocationService,
            mockWeatherService,
            mockSharedPreferenceService,
            mockTimeService
        )
    }

    private fun setupWithView() {
        whenever(mockView.hasLocationPermission()).thenReturn(false)
        presenter.onStart(mockView)
        reset(mockView)
    }

    @Test
    fun whenPresenterStarts_andPermissionsGranted_fetchesDataAndUpdatesView() {
        val locationCaptor = argumentCaptor<(lat: Double, lon: Double) -> Unit>()
        val weatherCaptor = argumentCaptor<(DataResult<Weather>) -> Unit>()

        whenever(mockView.hasLocationPermission()).thenReturn(true)
        whenever(mockTimeService.getCurrentTime()).thenReturn(fakeTime)

        presenter.onStart(mockView)

        verify(mockView).hasLocationPermission()
        verify(mockView).showLoading()

        verify(mockLocationService).getLocation(locationCaptor.capture())
        locationCaptor.firstValue.invoke(fakeLat, fakeLon)

        verify(mockWeatherService).getWeather(eq(fakeLat), eq(fakeLon), weatherCaptor.capture())
        weatherCaptor.firstValue.invoke(DataResult.Success(fakeWeather))

        verify(mockView).updateWeather(fakeWeather)
        verify(mockTimeService).getCurrentTime()
        verify(mockSharedPreferenceService).saveWeather(WeatherPresenter.PREF_CACHE, fakeWeather)
        verify(mockSharedPreferenceService).saveLong(WeatherPresenter.PREF_UPDATE_TIME, fakeTime)
    }

    @Test
    fun whenPresenterStarts_andPermissionsNotGranted_requestsPermission() {
        whenever(mockView.hasLocationPermission()).thenReturn(false)

        presenter.onStart(mockView)

        verify(mockView).requestLocationPermission()
    }

    @Test
    fun whenRefreshButtonClicked_checksPermission_andFetchesData() {
        setupWithView()

        whenever(mockView.hasLocationPermission()).thenReturn(true)

        presenter.onRefreshButtonClicked()

        verify(mockView).hasLocationPermission()
        verify(mockView).showLoading()
        verify(mockLocationService).getLocation(any())
    }

    @Test
    fun whenLocationPermissionGranted_itFetchesData() {
        setupWithView()

        presenter.onLocationPermissionGranted()

        verify(mockView).showLoading()
        verify(mockLocationService).getLocation(any())
    }

    @Test
    fun whenLocationPermissionDenied_updatesView() {
        setupWithView()

        presenter.onLocationPermissionDenied()

        verify(mockView).hideLoading()
        verify(mockView).showPermissionError()
    }

    @Test
    fun whenWeatherServiceFails_fetchesCachedDataAndUpdatesView() {
        val locationCaptor = argumentCaptor<(lat: Double, lon: Double) -> Unit>()
        val weatherCaptor = argumentCaptor<(DataResult<Weather>) -> Unit>()

        whenever(mockView.hasLocationPermission()).thenReturn(true)
        whenever(mockTimeService.timeDifferenceInHours(any())).thenReturn(12)
        whenever(mockTimeService.getRelativeTime(any())).thenReturn("12 Hours Ago")

        whenever(mockSharedPreferenceService.getLong(WeatherPresenter.PREF_UPDATE_TIME)).thenReturn(fakeTime)
        whenever(mockSharedPreferenceService.hasSavedValue(WeatherPresenter.PREF_CACHE)).thenReturn(true)
        whenever(mockSharedPreferenceService.getWeather(WeatherPresenter.PREF_CACHE)).thenReturn(fakeWeather)

        presenter.onStart(mockView)

        verify(mockView).hasLocationPermission()
        verify(mockView).showLoading()

        verify(mockLocationService).getLocation(locationCaptor.capture())
        locationCaptor.firstValue.invoke(fakeLat, fakeLon)

        verify(mockWeatherService).getWeather(eq(fakeLat), eq(fakeLon), weatherCaptor.capture())
        weatherCaptor.firstValue.invoke(DataResult.Failure(Exception("Some Error")))

        verify(mockView).updateWeather(fakeWeather)
        verify(mockView).showCachedDataInfo(fakeWeather.location, "12 Hours Ago")
        verify(mockTimeService).getRelativeTime(fakeTime)

        verify(mockSharedPreferenceService).getWeather(WeatherPresenter.PREF_CACHE)
        verify(mockSharedPreferenceService).getLong(WeatherPresenter.PREF_UPDATE_TIME)
    }

    @Test
    fun whenWeatherServiceFails_andNoValidCachedData_updatesView() {
        val locationCaptor = argumentCaptor<(lat: Double, lon: Double) -> Unit>()
        val weatherCaptor = argumentCaptor<(DataResult<Weather>) -> Unit>()

        whenever(mockView.hasLocationPermission()).thenReturn(true)
        whenever(mockTimeService.timeDifferenceInHours(any())).thenReturn(69)
        whenever(mockSharedPreferenceService.getLong(WeatherPresenter.PREF_UPDATE_TIME)).thenReturn(fakeTime)

        presenter.onStart(mockView)

        verify(mockView).hasLocationPermission()
        verify(mockView).showLoading()

        verify(mockLocationService).getLocation(locationCaptor.capture())
        locationCaptor.firstValue.invoke(fakeLat, fakeLon)

        verify(mockWeatherService).getWeather(eq(fakeLat), eq(fakeLon), weatherCaptor.capture())
        weatherCaptor.firstValue.invoke(DataResult.Failure(Exception("Some Error")))

        verify(mockSharedPreferenceService).getLong(WeatherPresenter.PREF_UPDATE_TIME)
        verify(mockTimeService).timeDifferenceInHours(fakeTime)
        verify(mockView).showNetworkError()
    }

}
