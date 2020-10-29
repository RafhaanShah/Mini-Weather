package com.miniweather.ui

import com.miniweather.model.DataResult
import com.miniweather.model.Location
import com.miniweather.service.location.LocationService
import com.miniweather.service.util.TimeService
import com.miniweather.service.weather.WeatherService
import com.miniweather.testutil.BaseTest
import com.miniweather.testutil.FakeDataProvider
import com.miniweather.ui.weather.WeatherContract
import com.miniweather.ui.weather.WeatherPresenter
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class WeatherPresenterTest : BaseTest() {

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
    private val fakeLocation = Location(1.111, 2.222)
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

    @Test
    fun whenViewAttachedWithPermissions_fetchesDataAndUpdatesView() = runBlockingTest {
        whenever(mockView.requestLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).thenReturn(fakeLocation)
        whenever(mockWeatherService.getWeather(any())).thenReturn(DataResult.Success(fakeWeather))
        whenever(mockTimeService.getCurrentTime()).thenReturn(fakeTimestamp)

        presenter.onAttachView(mockView)

        verify(mockView).requestLocationPermission()
        verify(mockView).showLoading()
        verify(mockLocationService).getLocation()
        verify(mockWeatherService).getWeather(fakeLocation)
        verify(mockView).showWeather(fakeWeather)
    }

    @Test
    fun whenRefreshButtonClicked_andPermissionGranted_andFetchesData() = runBlockingTest {
        setupWithLocationDenied()

        whenever(mockView.requestLocationPermission()).thenReturn(true)

        presenter.onRefreshButtonClicked()

        verify(mockView).requestLocationPermission()
        verify(mockView).showLoading()
        verify(mockLocationService).getLocation()
    }

    @Test
    fun whenRefreshButtonClicked_andPermissionDenied_updatesView() = runBlockingTest {
        setupWithLocationDenied()

        whenever(mockView.requestLocationPermission()).thenReturn(false)

        presenter.onRefreshButtonClicked()

        verify(mockView).requestLocationPermission()
        verify(mockView).showPermissionError()
        verifyZeroInteractions(mockLocationService)
    }

    @Test
    fun whenWeatherServiceReturnsCachedData_updatesView() = runBlockingTest {
        val fakeWeather = fakeWeather.copy(timestamp = fakeTimestamp - TimeUnit.MINUTES.toMillis(10))

        whenever(mockView.requestLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).thenReturn(fakeLocation)
        whenever(mockWeatherService.getWeather(any())).thenReturn(DataResult.Success(fakeWeather))
        whenever(mockTimeService.getCurrentTime()).thenReturn(fakeTimestamp)
        whenever(mockTimeService.getRelativeTimeString(any())).thenReturn("12 Hours ago")

        presenter.onAttachView(mockView)

        verify(mockView).showWeather(fakeWeather)
        verify(mockView).showLastUpdatedInfo(fakeWeather.location, "12 Hours ago")
        verify(mockTimeService).getRelativeTimeString(fakeWeather.timestamp)
    }

    @Test
    fun whenWeatherServiceFails_updatesView() = runBlockingTest {
        whenever(mockView.requestLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).thenReturn(fakeLocation)
        whenever(mockWeatherService.getWeather(any())).thenReturn(DataResult.Failure(Exception("Something went wrong")))

        presenter.onAttachView(mockView)

        verify(mockView).showNetworkError()
    }

    @Test
    fun whenLocationServiceTimesOut_updatesView() = runBlockingTest {
        whenever(mockView.requestLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).doThrow(mock<TimeoutCancellationException>())

        presenter.onAttachView(mockView)

        verify(mockView).showLocationError()
    }

    private suspend fun setupWithLocationDenied() {
        whenever(mockView.requestLocationPermission()).thenReturn(false)
        presenter.onAttachView(mockView)
        clearInvocations(mockView)
    }

}
