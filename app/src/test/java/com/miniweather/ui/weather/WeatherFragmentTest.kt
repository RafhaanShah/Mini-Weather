package com.miniweather.ui.weather

import android.Manifest
import coil.ImageLoader
import com.google.common.truth.Truth.assertThat
import com.miniweather.pages.WeatherPage
import com.miniweather.service.permissions.PermissionService
import com.miniweather.testutil.BaseFragmentTest
import com.miniweather.testutil.fakeError
import com.miniweather.testutil.fakeWeather
import com.miniweather.testutil.onPage
import com.miniweather.util.load
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherFragmentTest : BaseFragmentTest<WeatherFragment>(WeatherFragment::class.java) {

    @MockK
    private lateinit var mockImageLoader: ImageLoader

    @MockK
    private lateinit var mockPermissionsService: PermissionService

    @MockK
    private lateinit var mockPresenter: WeatherContract.Presenter

    @Before
    fun setup() {
        mockkStatic(ImageLoader::load)
        every { mockImageLoader.load(any(), any()) } just Runs

        launchFragment(
            WeatherFragment().apply {
                this.imageLoader = mockImageLoader
                this.permissionService = mockPermissionsService
                this.presenter = mockPresenter
            }
        )
    }

    @Test
    fun whenShowWeather_updatesWeatherCard() {
        scenario.onFragment { fragment ->
            fragment.showWeather(fakeWeather)
        }

        onPage(WeatherPage()) {
            shouldShowWeather(fakeWeather)
        }

        verify { mockImageLoader.load(any(), fakeWeather.iconUrl) }
    }

    @Test
    fun whenRefreshButtonClicked_callsPresenter() {
        scenario.onFragment { fragment ->
            fragment.showWeather(fakeWeather)
        }

        onPage(WeatherPage()) {
            pressRefresh()
        }

        verify { mockPresenter.onRefreshButtonClicked() }
    }

    @Test
    fun whenShowCachedDataInfo_showsLastUpdatedText() {
        val fakeTime = "12 hours ago"

        scenario.onFragment { fragment ->
            fragment.showWeather(fakeWeather)
            fragment.showLastUpdatedInfo(fakeWeather.location, fakeTime)
        }

        onPage(WeatherPage()) {
            shouldShowLastUpdated(fakeTime, fakeWeather.location)
        }
    }

    @Test
    fun whenShowLoading_showsSpinnerAndHidesOtherViews() {
        scenario.onFragment { fragment ->
            fragment.showLoading()
        }

        onPage(WeatherPage()) {
            shouldShowLoading()
        }
    }

    @Test
    fun whenShowError_showsErrorMessage() {
        scenario.onFragment { fragment ->
            fragment.showError(fakeError)
        }

        onPage(WeatherPage()) {
            shouldShowErrorMessage(fakeError)
        }
    }

    @Test
    fun whenRequestLocationPermission_callsPermissionService() = runBlockingTest {
        val expected = true
        coEvery {
            mockPermissionsService.request(any(), Manifest.permission.ACCESS_FINE_LOCATION)
        } returns expected

        scenario.onFragment { fragment ->
            launch {
                val actual = fragment.getLocationPermission()
                assertThat(actual).isEqualTo(expected)
            }
        }
    }
}
