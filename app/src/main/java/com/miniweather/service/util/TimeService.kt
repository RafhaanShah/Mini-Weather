package com.miniweather.service.util

import android.text.format.DateUtils

class TimeService {

    fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    fun getRelativeTimeString(time: Long): String {
        return DateUtils.getRelativeTimeSpanString(time, getCurrentTime(), DateUtils.MINUTE_IN_MILLIS).toString()
    }

}
