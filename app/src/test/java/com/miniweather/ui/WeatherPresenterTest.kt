package com.miniweather.ui

import com.miniweather.model.DataResult
import com.miniweather.service.location.LocationService
import com.miniweather.service.util.TimeService
import com.miniweather.service.weather.WeatherService
import com.miniweather.testutil.FakeDataProvider
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherPresenterTest {

    @Mock
    private lateinit var mockLocationService: LocationService

    @Mock
    private lateinit var mockWeatherService: WeatherService

    @Mock
    private lateinit var mockTimeService: TimeService

    @Mock
    private lateinit var mockView: WeatherContract.View

    private lateinit var presenter: WeatherPresenter

    private val testDispatcher = TestCoroutineDispatcher()

    private val fakeTimestamp: Long = 1000L
    private val fakeLat = 1.111
    private val fakeLon = 2.222
    private val fakeWeather = FakeDataProvider.provideFakeWeather()

    @Before
    fun setup() {
        presenter = WeatherPresenter(
            mockLocationService,
            mockTimeService,
            mockWeatherService,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    private fun setupWithView() {
        whenever(mockView.hasLocationPermission()).thenReturn(false)
        presenter.onStart(mockView)
        reset(mockView)
    }

    @Test
    fun whenPresenterStarts_andPermissionsGranted_fetchesDataAndUpdatesView() = runBlockingTest {
        val locationCaptor = argumentCaptor<(lat: Double, lon: Double) -> Unit>()

        whenever(mockView.hasLocationPermission()).thenReturn(true)
        whenever(mockTimeService.getCurrentTime()).thenReturn(fakeTimestamp)
        whenever(mockWeatherService.getWeather(any(), any())).thenReturn(DataResult.Success(fakeWeather))

        presenter.onStart(mockView)

        verify(mockView).hasLocationPermission()
        verify(mockView).showLoading()

        verify(mockLocationService).getLocation(locationCaptor.capture())
        locationCaptor.firstValue.invoke(fakeLat, fakeLon)

        verify(mockWeatherService).getWeather(fakeLat, fakeLon)
        verify(mockView).updateWeather(fakeWeather)
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

        verify(mockView).showPermissionError()
    }

    @Test
    fun whenWeatherServiceReturnsCachedData_updatesView() = runBlockingTest {
        val fakeWeather = fakeWeather.copy(timestamp = fakeTimestamp - TimeUnit.MINUTES.toMillis(10))
        val locationCaptor = argumentCaptor<(lat: Double, lon: Double) -> Unit>()

        whenever(mockView.hasLocationPermission()).thenReturn(true)
        whenever(mockTimeService.getRelativeTimeString(any())).thenReturn("12 Hours ago")
        whenever(mockWeatherService.getWeather(any(), any())).thenReturn(DataResult.Success(fakeWeather))

        presenter.onStart(mockView)

        verify(mockView).hasLocationPermission()
        verify(mockView).showLoading()

        verify(mockLocationService).getLocation(locationCaptor.capture())
        locationCaptor.firstValue.invoke(fakeLat, fakeLon)

        verify(mockWeatherService).getWeather(fakeLat, fakeLon)

        verify(mockView).updateWeather(fakeWeather)
        verify(mockView).showCachedDataInfo(fakeWeather.location, "12 Hours ago")
        verify(mockTimeService).getRelativeTimeString(fakeWeather.timestamp)
    }

    @Test
    fun whenWeatherServiceFails_updatesView() = runBlockingTest {
        val locationCaptor = argumentCaptor<(lat: Double, lon: Double) -> Unit>()

        whenever(mockView.hasLocationPermission()).thenReturn(true)
        whenever(mockWeatherService.getWeather(any(), any()))
            .thenReturn(DataResult.Failure(Exception("Something went wrong")))

        presenter.onStart(mockView)

        verify(mockView).hasLocationPermission()
        verify(mockView).showLoading()

        verify(mockLocationService).getLocation(locationCaptor.capture())
        locationCaptor.firstValue.invoke(fakeLat, fakeLon)

        verify(mockWeatherService).getWeather(fakeLat, fakeLon)
        verify(mockView).showNetworkError()
    }

}
