package com.miniweather.service.util

import android.content.res.Resources
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import javax.inject.Inject

class StringResourceService @Inject constructor(private val resources: Resources) {

    fun getString(@StringRes id: Int): String {
        return resources.getString(id)
    }

    fun getStringArray(@ArrayRes id: Int): Array<String> {
        return resources.getStringArray(id)
    }

}
