package com.miniweather.ui

import android.Manifest
import com.miniweather.pages.WeatherPage
import com.miniweather.service.network.ImageService
import com.miniweather.service.permissions.PermissionService
import com.miniweather.testutil.BaseActivityTest
import com.miniweather.testutil.fakeError
import com.miniweather.testutil.fakeWeather
import com.miniweather.testutil.onPage
import com.miniweather.ui.weather.WeatherActivity
import com.miniweather.ui.weather.WeatherContract
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

@ExperimentalCoroutinesApi
class WeatherActivityTest : BaseActivityTest<WeatherActivity>(WeatherActivity::class.java) {

    @Mock
    private lateinit var mockPresenter: WeatherContract.Presenter

    @Mock
    private lateinit var mockImageService: ImageService

    @Mock
    private lateinit var mockPermissionsService: PermissionService

    @Before
    override fun setup() {
        super.setup()
        scenario.onActivity { activity ->
            activity.presenter = mockPresenter
            activity.imageService = mockImageService
            activity.permissionService = mockPermissionsService
        }
    }

    @Test
    fun whenShowWeather_updatesWeatherCard() {
        scenario.onActivity { activity ->
            activity.showWeather(fakeWeather)
        }

        onPage(WeatherPage()) {
            shouldShowWeather(fakeWeather)
        }

        verify(mockImageService).loadImage(any(), eq(fakeWeather.iconUrl), any())
    }

    @Test
    fun whenRefreshButtonClicked_callsPresenter() {
        scenario.onActivity { activity ->
            activity.showWeather(fakeWeather)
        }

        onPage(WeatherPage()) {
            pressRefresh()
        }

        verify(mockPresenter).onRefreshButtonClicked()
    }

    @Test
    fun whenShowCachedDataInfo_showsLastUpdatedText() {
        val fakeTime = "12 hours ago"

        scenario.onActivity { activity ->
            activity.showWeather(fakeWeather)
            activity.showLastUpdatedInfo(fakeWeather.location, fakeTime)
        }

        onPage(WeatherPage()) {
            shouldShowLastUpdated(fakeTime, fakeWeather.location)
        }
    }

    @Test
    fun whenShowLoading_showsSpinnerAndHidesOtherViews() {
        scenario.onActivity { activity ->
            activity.showLoading()
        }

        onPage(WeatherPage()) {
            shouldShowLoading()
        }
    }

    @Test
    fun whenShowError_showsErrorMessage() {
        scenario.onActivity { activity ->
            activity.showError(fakeError)
        }

        onPage(WeatherPage()) {
            shouldShowErrorMessage(fakeError)
        }
    }

    @Test
    fun whenRequestLocationPermission_callsPermissionService() = runBlockingTest {
        whenever(mockPermissionsService.requestPermission(any(), any())).thenReturn(true)

        scenario.onActivity { activity ->
            launch {
                val actual = activity.requestLocationPermission()
                assertTrue(actual)
                verify(mockPermissionsService).requestPermission(
                    any(),
                    eq(Manifest.permission.ACCESS_FINE_LOCATION)
                )
            }
        }
    }

}
