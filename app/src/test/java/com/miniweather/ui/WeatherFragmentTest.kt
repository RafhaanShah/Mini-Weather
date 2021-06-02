package com.miniweather.ui

import android.Manifest
import com.miniweather.pages.WeatherPage
import com.miniweather.service.network.ImageService
import com.miniweather.service.permissions.PermissionService
import com.miniweather.testutil.BaseFragmentTest
import com.miniweather.testutil.fakeError
import com.miniweather.testutil.fakeWeather
import com.miniweather.testutil.onPage
import com.miniweather.ui.weather.WeatherContract
import com.miniweather.ui.weather.WeatherFragment
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

@ExperimentalCoroutinesApi
class WeatherFragmentTest : BaseFragmentTest<WeatherFragment>(WeatherFragment::class.java) {

    @Mock
    private lateinit var mockImageService: ImageService

    @Mock
    private lateinit var mockPermissionsService: PermissionService

    @Mock
    private lateinit var mockPresenter: WeatherContract.Presenter

    @Before
    fun setup() {
        launchFragment(WeatherFragment().apply {
            this.imageService = mockImageService
            this.permissionService = mockPermissionsService
            this.presenter = mockPresenter
        })
    }

    @Test
    fun whenShowWeather_updatesWeatherCard() {
        scenario.onFragment { fragment ->
            fragment.showWeather(fakeWeather)
        }

        onPage(WeatherPage()) {
            shouldShowWeather(fakeWeather)
        }

        verify(mockImageService).loadImage(any(), eq(fakeWeather.iconUrl), any())
    }

    @Test
    fun whenRefreshButtonClicked_callsPresenter() {
        scenario.onFragment { fragment ->
            fragment.showWeather(fakeWeather)
        }

        onPage(WeatherPage()) {
            pressRefresh()
        }

        verify(mockPresenter).onRefreshButtonClicked()
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
        whenever(
            mockPermissionsService
                .requestPermission(any(), eq(Manifest.permission.ACCESS_FINE_LOCATION))
        )
            .thenReturn(expected)

        scenario.onFragment { fragment ->
            launch {
                val actual = fragment.getLocationPermission()
                assertEquals(expected, actual)
            }
        }
    }

}
