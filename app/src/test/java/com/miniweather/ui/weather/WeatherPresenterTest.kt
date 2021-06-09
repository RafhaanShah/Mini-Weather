package com.miniweather.ui.weather

import com.miniweather.provider.DateTimeProvider
import com.miniweather.provider.ResourceProvider
import com.miniweather.repository.WeatherRepository
import com.miniweather.service.location.LocationService
import com.miniweather.testutil.*
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class WeatherPresenterTest : BaseTest() {

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    @Mock
    private lateinit var mockLocationService: LocationService

    @Mock
    private lateinit var mockWeatherRepository: WeatherRepository

    @Mock
    private lateinit var mockDateTimeProvider: DateTimeProvider

    @Mock
    private lateinit var mockResourceProvider: ResourceProvider

    @Mock
    private lateinit var mockView: WeatherContract.View

    private lateinit var presenter: WeatherPresenter

    @Before
    fun setup() {
        presenter = WeatherPresenter(
            mockLocationService,
            mockDateTimeProvider,
            mockWeatherRepository,
            mockResourceProvider,
            coroutinesTestRule.testDispatcher
        )
    }

    @Test
    fun whenViewAttachedWithPermissions_fetchesDataAndUpdatesView() = runBlockingTest {
        whenever(mockView.getLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).thenReturn(fakeLocation)
        whenever(mockWeatherRepository.getWeather(any())).thenReturn(Result.success(fakeWeather))
        whenever(mockDateTimeProvider.getCurrentTime()).thenReturn(fakeTimestamp)

        presenter.onAttachView(mockView)

        verify(mockView).getLocationPermission()
        verify(mockView).showLoading()
        verify(mockLocationService).getLocation()
        verify(mockWeatherRepository).getWeather(fakeLocation)
        verify(mockView).showWeather(fakeWeather)
    }

    @Test
    fun whenRefreshButtonClicked_andPermissionGranted_andFetchesData() = runBlockingTest {
        setupWithLocationDenied()

        whenever(mockView.getLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).thenReturn(fakeLocation)
        whenever(mockWeatherRepository.getWeather(any())).thenReturn(Result.success(fakeWeather))
        whenever(mockDateTimeProvider.getCurrentTime()).thenReturn(fakeTimestamp)

        presenter.onRefreshButtonClicked()

        verify(mockView).getLocationPermission()
        verify(mockView).showLoading()
        verify(mockLocationService).getLocation()
    }

    @Test
    fun whenRefreshButtonClicked_andPermissionDenied_updatesView() = runBlockingTest {
        whenever(mockResourceProvider.getString(any())).thenReturn(fakeError)
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
        whenever(mockWeatherRepository.getWeather(any())).thenReturn(Result.success(fakeWeather))
        whenever(mockDateTimeProvider.getCurrentTime()).thenReturn(fakeTimestamp)
        whenever(mockDateTimeProvider.getRelativeTimeString(any())).thenReturn(fakeTime)

        presenter.onAttachView(mockView)

        verify(mockView).showWeather(fakeWeather)
        verify(mockView).showLastUpdatedInfo(fakeWeather.location, fakeTime)
        verify(mockDateTimeProvider).getRelativeTimeString(fakeWeather.timestamp)
    }

    @Test
    fun whenWeatherServiceFails_updatesView() = runBlockingTest {
        whenever(mockResourceProvider.getString(any())).thenReturn(fakeError)
        whenever(mockView.getLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getLocation()).thenReturn(fakeLocation)
        whenever(mockWeatherRepository.getWeather(any())).thenReturn(
            Result.failure(
                Exception(
                    fakeError
                )
            )
        )

        presenter.onAttachView(mockView)

        verify(mockView).showError(fakeError)
    }

    @Test
    fun whenLocationServiceTimesOut_updatesView() = runBlockingTest {
        whenever(mockResourceProvider.getString(any())).thenReturn(fakeError)
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
