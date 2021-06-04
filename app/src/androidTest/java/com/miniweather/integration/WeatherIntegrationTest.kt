package com.miniweather.integration

import androidx.test.rule.GrantPermissionRule
import com.miniweather.pages.WeatherPage
import com.miniweather.service.network.weatherPath
import com.miniweather.testutil.BaseIntegrationTest
import com.miniweather.testutil.fakeTimestamp
import com.miniweather.testutil.fakeWeather
import com.miniweather.testutil.onPage
import com.miniweather.ui.weather.WeatherFragment
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class WeatherIntegrationTest : BaseIntegrationTest<WeatherFragment>(WeatherFragment::class.java) {

    @get:Rule
    val locationPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Test
    fun testWeatherJourney() {
        expectHttpRequest(path = weatherPath, fileName = "weather.json")
        launchFragment()

        onPage(WeatherPage()) {
            shouldShowWeather(fakeWeather)
        }
    }

    @Test
    fun testOfflineWeatherJourney() {
        expectHttpRequest(path = weatherPath, code = 500)
        executeDbOperation {
            weatherDao().insertIntoCache(
                fakeWeather.copy(timestamp = fakeTimestamp - TimeUnit.MINUTES.toMillis(10))
            )
        }

        launchFragment()

        onPage(WeatherPage()) {
            shouldShowWeather(fakeWeather, cached = true)
            shouldShowLastUpdated("10 minutes ago", fakeWeather.location)
        }
    }

}
