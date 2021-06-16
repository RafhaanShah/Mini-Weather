package com.miniweather.ui.weather

import com.miniweather.mapper.ErrorMapper
import com.miniweather.mapper.ErrorType
import com.miniweather.provider.DateTimeProvider
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
    private lateinit var mockErrorMapper: ErrorMapper

    @MockK
    private lateinit var mockView: WeatherContract.View

    private lateinit var presenter: WeatherPresenter

    @Before
    fun setup() {
        every { mockDateTimeProvider.getCurrentTime() } returns fakeTimestamp
        coEvery { mockLocationService.getLocation() } returns fakeLocation
        coEvery { mockWeatherRepository.getWeather(anyValue()) } returns value(
            Result.success(fakeWeather)
        )

        presenter = WeatherPresenter(
            mockLocationService,
            mockDateTimeProvider,
            mockWeatherRepository,
            mockErrorMapper,
            coroutinesTestRule.testDispatcher
        )
    }

    @Test
    fun whenViewAttachedWithPermissions_fetchesDataAndUpdatesView() = runBlockingTest {
        attachPresenter(locationPermission = true)

        coVerify {
            mockView.getLocationPermission()
            mockView.showLoading()
            mockView.showWeather(fakeWeather)
        }
        verify(exactly = 0) { mockView.showLastUpdatedInfo(any(), any()) }
    }

    @Test
    fun whenAttachedWithoutPermissions_andRefreshButtonClicked_andPermissionDenied_updatesView() =
        runBlockingTest {
            every { mockErrorMapper.mapError(ErrorType.LOCATION_PERMISSION) } returns fakeError
            attachPresenter(locationPermission = false)

            presenter.onRefreshButtonClicked()

            coVerify(exactly = 2) {
                mockView.getLocationPermission()
                mockView.showError(fakeError)
            }
            verify { mockLocationService wasNot Called }
        }

    @Test
    fun whenRefreshButtonClicked_andPermissionGranted_fetchesDataAndUpdatesView() =
        runBlockingTest {
            every { mockErrorMapper.mapError(ErrorType.LOCATION_PERMISSION) } returns fakeError
            attachPresenter(locationPermission = false)

            coEvery { mockView.getLocationPermission() } returns true

            presenter.onRefreshButtonClicked()

            coVerify(exactly = 2) {
                mockView.getLocationPermission()
            }
            verify {
                mockView.showLoading()
                mockView.showWeather(fakeWeather)
            }
        }

    @Test
    fun whenWeatherRepositoryReturnsCachedData_updatesView() = runBlockingTest {
        val oldFakeWeather =
            fakeWeather.copy(timestamp = fakeTimestamp - TimeUnit.MINUTES.toMillis(10))
        val fakeTime = "10 minutes ago"
        coEvery { mockWeatherRepository.getWeather(anyValue()) } returns value(
            Result.success(oldFakeWeather)
        )
        every { mockDateTimeProvider.getRelativeTimeString(any()) } returns fakeTime

        attachPresenter(locationPermission = true)

        verify {
            mockView.showWeather(oldFakeWeather)
            mockView.showLastUpdatedInfo(oldFakeWeather.location, fakeTime)
        }
    }

    @Test
    fun whenWeatherRepositoryFails_updatesView() = runBlockingTest {
        every { mockErrorMapper.mapNetworkException(any()) } returns fakeError
        coEvery { mockWeatherRepository.getWeather(anyValue()) } returns value(
            Result.failure(Exception(fakeError))
        )

        attachPresenter(locationPermission = true)

        verify { mockView.showError(fakeError) }
    }

    @Test
    fun whenLocationServiceTimesOut_updatesView() = runBlockingTest {
        every { mockErrorMapper.mapError(ErrorType.LOCATION_TIMEOUT) } returns fakeError
        coEvery {
            mockLocationService.getLocation()
        } throws mockk<TimeoutCancellationException>()

        attachPresenter(locationPermission = true)

        verify { mockView.showError(fakeError) }
    }

    private fun attachPresenter(locationPermission: Boolean) {
        coEvery { mockView.getLocationPermission() } returns locationPermission
        presenter.onAttachView(mockView)
    }

}
