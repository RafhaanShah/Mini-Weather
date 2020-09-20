package com.miniweather.service

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferenceService @Inject constructor(private val sharedPref: SharedPreferences) {

    fun saveString(key: String, value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun saveInt(key: String, value: Int) {
        with(sharedPref.edit()) {
            putInt(key, value)
            commit()
        }
    }

    fun saveLong(key: String, value: Long) {
        with(sharedPref.edit()) {
            putLong(key, value)
            commit()
        }
    }

    fun getString(key: String): String {
        return sharedPref.getString(key, "") ?: ""
    }

    fun getInt(key: String): Int {
        return sharedPref.getInt(key, 0)
    }

    fun getLong(key: String): Long {
        return sharedPref.getLong(key, 0)
    }
}
