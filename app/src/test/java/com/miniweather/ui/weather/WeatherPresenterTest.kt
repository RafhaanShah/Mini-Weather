package com.miniweather.ui.weather

import com.miniweather.provider.DateTimeProvider
import com.miniweather.provider.ResourceProvider
import com.miniweather.repository.WeatherRepository
import com.miniweather.service.location.LocationService
import com.miniweather.testutil.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class WeatherPresenterTest : BaseTest() {

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    @MockK
    private lateinit var mockLocationService: LocationService

    @MockK
    private lateinit var mockWeatherRepository: WeatherRepository

    @MockK
    private lateinit var mockDateTimeProvider: DateTimeProvider

    @MockK
    private lateinit var mockResourceProvider: ResourceProvider

    @MockK
    private lateinit var mockView: WeatherContract.View

    private lateinit var presenter: WeatherPresenter

    @Before
    fun setup() {
        every { mockResourceProvider.getString(any()) } returns fakeError

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
        coEvery { mockView.getLocationPermission() } returns true
        coEvery { mockLocationService.getLocation() } returns fakeLocation
        coEvery { mockWeatherRepository.getWeather(anyValue()) } returns value(
            Result.success(
                fakeWeather
            )
        )
        every { mockDateTimeProvider.getCurrentTime() } returns fakeTimestamp

        presenter.onAttachView(mockView)

        coVerify { mockView.getLocationPermission() }
        verify { mockView.showLoading() }
        coVerify { mockLocationService.getLocation() }
        coVerify { mockWeatherRepository.getWeather(fakeLocation) }
        verify { mockView.showWeather(fakeWeather) }
    }

    @Test
    fun whenRefreshButtonClicked_andPermissionGranted_andFetchesData() = runBlockingTest {
        coEvery { mockView.getLocationPermission() } returns false
        presenter.onAttachView(mockView)

        coEvery { mockView.getLocationPermission() } returns true
        coEvery { mockLocationService.getLocation() } returns fakeLocation
        coEvery { mockWeatherRepository.getWeather(anyValue()) } returns value(
            Result.success(
                fakeWeather
            )
        )
        every { mockDateTimeProvider.getCurrentTime() } returns fakeTimestamp

        presenter.onRefreshButtonClicked()

        coVerify {
            mockView.getLocationPermission()
        }
        verify {
            mockView.showLoading()
        }
        coVerify {
            mockLocationService.getLocation()
        }
    }

    @Test
    fun whenRefreshButtonClicked_andPermissionDenied_updatesView() = runBlockingTest {
        coEvery { mockView.getLocationPermission() } returns false
        presenter.onAttachView(mockView)

        coEvery { mockView.getLocationPermission() } returns false

        presenter.onRefreshButtonClicked()

        coVerify {
            mockView.getLocationPermission()
        }
        verify {
            mockView.showError(fakeError)
        }
        verify {
            mockLocationService wasNot Called
        }
    }

    @Test
    fun whenWeatherServiceReturnsCachedData_updatesView() = runBlockingTest {
        val fakeWeather =
            fakeWeather.copy(
                timestamp = fakeTimestamp - TimeUnit.MINUTES.toMillis(
                    10
                )
            )
        val fakeTime = "12 hours ago"

        coEvery { mockView.getLocationPermission() } returns true
        coEvery { mockLocationService.getLocation() } returns fakeLocation
        coEvery { mockWeatherRepository.getWeather(anyValue()) } returns value(
            Result.success(
                fakeWeather
            )
        )
        every { mockDateTimeProvider.getCurrentTime() } returns fakeTimestamp
        every { mockDateTimeProvider.getRelativeTimeString(any()) } returns fakeTime

        presenter.onAttachView(mockView)

        verify {
            mockView.showWeather(fakeWeather)
        }
        verify {
            mockView.showLastUpdatedInfo(fakeWeather.location, fakeTime)
        }
        verify {
            mockDateTimeProvider.getRelativeTimeString(fakeWeather.timestamp)
        }

    }

    @Test
    fun whenWeatherServiceFails_updatesView() = runBlockingTest {
        coEvery { mockView.getLocationPermission() } returns true
        coEvery { mockLocationService.getLocation() } returns fakeLocation
        coEvery { mockWeatherRepository.getWeather(anyValue()) } returns value(
            Result.failure(
                Exception(
                    fakeError
                )
            )
        )

        presenter.onAttachView(mockView)

        verify {
            mockView.showError(fakeError)
        }
    }

    @Test
    fun whenLocationServiceTimesOut_updatesView() =
        runBlockingTest {
            coEvery { mockView.getLocationPermission() } returns true
            coEvery {
                mockLocationService.getLocation()
            } throws mockk<TimeoutCancellationException>()

            presenter.onAttachView(mockView)

            verify {
                mockView.showError(fakeError)
            }
        }

}
