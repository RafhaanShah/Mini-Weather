package com.miniweather.service

import android.text.format.DateUtils
import kotlin.math.abs

class TimeService {

    fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    fun getRelativeTime(time: Long): String {
        return DateUtils.getRelativeTimeSpanString(time, getCurrentTime(), DateUtils.MINUTE_IN_MILLIS).toString()
    }

    fun timeDifferenceInHours(time: Long): Long {
        val diff = abs(time - getCurrentTime())
        return diff / (60 * 60 * 1000)
    }
}
