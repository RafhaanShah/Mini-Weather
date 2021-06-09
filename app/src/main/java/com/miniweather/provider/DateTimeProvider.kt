package com.miniweather.provider

import android.text.format.DateUtils
import javax.inject.Inject

class DateTimeProvider @Inject constructor() {

    fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    fun getRelativeTimeString(time: Long): String {
        return DateUtils.getRelativeTimeSpanString(
            time,
            getCurrentTime(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }

}
