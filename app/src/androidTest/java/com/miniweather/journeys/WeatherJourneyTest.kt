package com.miniweather.journeys

import androidx.test.rule.GrantPermissionRule
import com.miniweather.pages.WeatherPage
import com.miniweather.service.network.weatherPath
import com.miniweather.testutil.BaseJourneyTest
import com.miniweather.testutil.fakeWeather
import com.miniweather.testutil.onPage
import com.miniweather.ui.weather.WeatherActivity
import org.junit.Rule
import org.junit.Test

class WeatherJourneyTest : BaseJourneyTest<WeatherActivity>(WeatherActivity::class.java) {

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Test
    fun testWeatherJourney() {
        expectHttpRequest(path = weatherPath, fileName = "weather.json")
        launch()

        onPage(WeatherPage()) {
            shouldShowWeather(fakeWeather)
        }
    }

}
