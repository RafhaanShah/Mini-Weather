package com.miniweather.service

import android.content.SharedPreferences
import com.miniweather.model.Weather
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SharedPreferenceService @Inject constructor(private val sharedPref: SharedPreferences) {

    fun hasSavedValue(key: String) = sharedPref.contains(key)

    fun saveWeather(key: String, value: Weather) {
        with(sharedPref.edit()) {
            putString(key, Json.encodeToString(value))
            commit()
        }
    }

    fun getWeather(key: String): Weather {
        return Json.decodeFromString(sharedPref.getString(key, null)!!)
    }

    fun saveLong(key: String, value: Long) {
        with(sharedPref.edit()) {
            putLong(key, value)
            commit()
        }
    }

    fun getLong(key: String) = sharedPref.getLong(key, 0)

}
