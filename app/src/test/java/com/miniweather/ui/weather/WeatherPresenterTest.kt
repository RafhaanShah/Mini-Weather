package com.miniweather.ui.weather

import com.miniweather.service.location.LocationService
import com.miniweather.service.util.StringResourceService
import com.miniweather.service.util.TimeService
import com.miniweather.service.weather.WeatherService
import com.miniweather.testutil.*
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
    private lateinit var mockStringResourceService: StringResourceService

    @Mock
    private lateinit var mockView: WeatherContract.View

    private lateinit var presenter: WeatherPresenter

    private val testDispatcher = TestCoroutineDispatcher()


    @Before
    fun setup() {
        presenter = WeatherPresenter(
            mockLocationService,
            mockTimeService,
            mockWeatherService,
            mockStringResourceService,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun whenViewAttachedWithPermissions_fetchesDataAndUpdatesView() = runBlockingTest {
        whenever(mockView.getLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).thenReturn(fakeLocation)
        whenever(mockWeatherService.getWeather(any())).thenReturn(Result.success(fakeWeather))
        whenever(mockTimeService.getCurrentTime()).thenReturn(fakeTimestamp)

        presenter.onAttachView(mockView)

        verify(mockView).getLocationPermission()
        verify(mockView).showLoading()
        verify(mockLocationService).getLocation()
        verify(mockWeatherService).getWeather(fakeLocation)
        verify(mockView).showWeather(fakeWeather)
    }

    @Test
    fun whenRefreshButtonClicked_andPermissionGranted_andFetchesData() = runBlockingTest {
        setupWithLocationDenied()

        whenever(mockView.getLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).thenReturn(fakeLocation)
        whenever(mockWeatherService.getWeather(any())).thenReturn(Result.success(fakeWeather))
        whenever(mockTimeService.getCurrentTime()).thenReturn(fakeTimestamp)

        presenter.onRefreshButtonClicked()

        verify(mockView).getLocationPermission()
        verify(mockView).showLoading()
        verify(mockLocationService).getLocation()
    }

    @Test
    fun whenRefreshButtonClicked_andPermissionDenied_updatesView() = runBlockingTest {
        whenever(mockStringResourceService.getString(any())).thenReturn(fakeError)
        setupWithLocationDenied()

        whenever(mockView.getLocationPermission()).thenReturn(false)

        presenter.onRefreshButtonClicked()

        verify(mockView).getLocationPermission()
        verify(mockView).showError(fakeError)
        verifyZeroInteractions(mockLocationService)
    }

    @Test
    fun whenWeatherServiceReturnsCachedData_updatesView() = runBlockingTest {
        val fakeWeather =
            fakeWeather.copy(timestamp = fakeTimestamp - TimeUnit.MINUTES.toMillis(10))
        val fakeTime = "12 hours ago"

        whenever(mockView.getLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).thenReturn(fakeLocation)
        whenever(mockWeatherService.getWeather(any())).thenReturn(Result.success(fakeWeather))
        whenever(mockTimeService.getCurrentTime()).thenReturn(fakeTimestamp)
        whenever(mockTimeService.getRelativeTimeString(any())).thenReturn(fakeTime)

        presenter.onAttachView(mockView)

        verify(mockView).showWeather(fakeWeather)
        verify(mockView).showLastUpdatedInfo(fakeWeather.location, fakeTime)
        verify(mockTimeService).getRelativeTimeString(fakeWeather.timestamp)
    }

    @Test
    fun whenWeatherServiceFails_updatesView() = runBlockingTest {
        whenever(mockStringResourceService.getString(any())).thenReturn(fakeError)
        whenever(mockView.getLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).thenReturn(fakeLocation)
        whenever(mockWeatherService.getWeather(any())).thenReturn(Result.failure(Exception(fakeError)))

        presenter.onAttachView(mockView)

        verify(mockView).showError(fakeError)
    }

    @Test
    fun whenLocationServiceTimesOut_updatesView() = runBlockingTest {
        whenever(mockStringResourceService.getString(any())).thenReturn(fakeError)
        whenever(mockView.getLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).doThrow(mock<TimeoutCancellationException>())

        presenter.onAttachView(mockView)

        verify(mockView).showError(fakeError)
    }

    private suspend fun setupWithLocationDenied() {
        whenever(mockView.getLocationPermission()).thenReturn(false)
        presenter.onAttachView(mockView)
        clearInvocations(mockView)
    }

}
