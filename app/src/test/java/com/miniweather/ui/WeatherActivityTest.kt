package com.miniweather.ui

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.miniweather.R
import com.miniweather.service.network.ImageService
import com.miniweather.service.permissions.PermissionService
import com.miniweather.testutil.BaseActivityTest
import com.miniweather.testutil.FakeDataProvider
import com.miniweather.ui.weather.WeatherActivity
import com.miniweather.ui.weather.WeatherContract
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
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

    private val fakeWeather = FakeDataProvider.provideFakeWeather()

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
    fun whenRefreshButtonClicked_callsPresenter() {
        onView(withId(R.id.weather_fab)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_fab)).perform(click())

        verify(mockPresenter).onRefreshButtonClicked()
    }

    @Test
    fun whenShowWeather_updatesWeatherCard() {
        scenario.onActivity { activity ->
            activity.showWeather(fakeWeather)
        }

        onView(withId(R.id.weather_card)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_last_updated_text)).check(matches(not(isDisplayed())))

        onView(withId(R.id.weather_condition_text)).check(matches(withText(containsString(fakeWeather.condition))))
        onView(withId(R.id.weather_temperature_text)).check(matches(withText(containsString(fakeWeather.temperature.toString()))))
        onView(withId(R.id.weather_wind_speed_text)).check(matches(withText(containsString(fakeWeather.windSpeed.toString()))))
        onView(withId(R.id.weather_wind_direction_text)).check(matches(withText(containsString(fakeWeather.windDirection))))

        onView(withId(R.id.weather_progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_error_message_card)).check(matches(not(isDisplayed())))

        verify(mockImageService).loadImage(any(), eq(fakeWeather.iconUrl), any())
    }

    @Test
    fun whenShowCachedDataInfo_showsLastUpdatedText() {
        scenario.onActivity { activity ->
            activity.showLastUpdatedInfo(fakeWeather.location, "12 hours ago")
        }

        onView(withId(R.id.weather_last_updated_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_last_updated_text)).check(matches(withText(containsString(fakeWeather.location))))
        onView(withId(R.id.weather_last_updated_text)).check(matches(withText(containsString("12 hours ago"))))
    }

    @Test
    fun whenShowLoading_showsSpinnerAndHidesOtherViews() {
        scenario.onActivity { activity ->
            activity.showLoading()
        }

        onView(withId(R.id.weather_progress)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_card)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_error_message_card)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_fab)).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenShowNetworkError_showsErrorMessage() {
        scenario.onActivity { activity ->
            activity.showNetworkError()
        }

        onView(withId(R.id.weather_error_message_card)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_error_message_text))
            .check(matches(withText(containsString(context.getString(R.string.error_network_request)))))

        onView(withId(R.id.weather_progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_card)).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenShowPermissionError_showsErrorMessage() {
        scenario.onActivity { activity ->
            activity.showPermissionError()
        }

        onView(withId(R.id.weather_error_message_card)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_error_message_text))
            .check(matches(withText(containsString(context.getString(R.string.error_permission_location)))))

        onView(withId(R.id.weather_progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_card)).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenRequestLocationPermission_callsPermissionService() = runBlockingTest {
        whenever(mockPermissionsService.requestPermission(any(), any())).thenReturn(true)

        scenario.onActivity { activity ->
            launch {
                val actual = activity.requestLocationPermission()
                assertTrue(actual)
                verify(mockPermissionsService).requestPermission(any(), eq(Manifest.permission.ACCESS_FINE_LOCATION))
            }
        }
    }

}
