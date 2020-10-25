package com.miniweather.ui

import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.miniweather.R
import com.miniweather.app.TestApplication
import com.miniweather.service.network.ImageService
import com.miniweather.testutil.FakeDataProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = TestApplication::class)
@RunWith(RobolectricTestRunner::class)
class WeatherActivityTest {

    @Mock
    private lateinit var mockPresenter: WeatherContract.Presenter
    @Mock
    private lateinit var mockImageService: ImageService

    private lateinit var activity: WeatherActivity

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val fakeWeather = FakeDataProvider.provideFakeWeather()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        val controller = Robolectric.buildActivity(WeatherActivity::class.java)

        activity = controller.setup().get()
        activity.presenter = mockPresenter
        activity.imageService = mockImageService
    }

    @Test
    fun whenRefreshButtonClicked_callsPresenter() {
        onView(withId(R.id.weather_fab)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_fab)).perform(click())

        verify(mockPresenter).onRefreshButtonClicked()
    }

    @Test
    fun whenUpdateWeatherCalled_updatesWeatherCard() {
        activity.showWeather(fakeWeather)

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
        activity.showLastUpdatedInfo(fakeWeather.location, "12 hours ago")

        onView(withId(R.id.weather_last_updated_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_last_updated_text)).check(matches(withText(containsString(fakeWeather.location))))
        onView(withId(R.id.weather_last_updated_text)).check(matches(withText(containsString("12 hours ago"))))
    }

    @Test
    fun whenShowLoading_showsSpinnerAndHidesOtherViews() {
        activity.showLoading()

        onView(withId(R.id.weather_progress)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_card)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_error_message_card)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_fab)).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenHideLoading_hidesSpinner_andShowsFab() {
        activity.hideLoading()

        onView(withId(R.id.weather_progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_fab)).check(matches(isDisplayed()))
    }

    @Test
    fun whenShowNetworkError_showsErrorMessage() {
        activity.showNetworkError()

        onView(withId(R.id.weather_error_message_card)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_error_message_text))
            .check(matches(withText(containsString(context.getString(R.string.error_network_request)))))

        onView(withId(R.id.weather_progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_card)).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenShowPermissionError_showsErrorMessage() {
        activity.showPermissionError()

        onView(withId(R.id.weather_error_message_card)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_error_message_text))
            .check(matches(withText(containsString(context.getString(R.string.error_permission_location)))))

        onView(withId(R.id.weather_progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.weather_card)).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenLocationPermissionGranted_callsPresenter() {
        activity.onRequestPermissionsResult(100, arrayOf(), intArrayOf(PackageManager.PERMISSION_GRANTED))
        verify(mockPresenter).onLocationPermissionGranted()
    }

    @Test
    fun whenLocationPermissionDenied_callsPresenter() {
        activity.onRequestPermissionsResult(100, arrayOf(), intArrayOf(PackageManager.PERMISSION_DENIED))
        verify(mockPresenter).onLocationPermissionDenied()
    }

    @Test
    fun whenRequestLocationPermission_requestsPermission() {
        val spy = spy(activity)
        spy.requestLocationPermission()
        verify(spy).requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
    }

}
