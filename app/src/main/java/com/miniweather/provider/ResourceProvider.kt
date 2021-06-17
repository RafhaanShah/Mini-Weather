package com.miniweather.provider

import android.content.res.Resources
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import javax.inject.Inject

class ResourceProvider @Inject constructor(private val resources: Resources) {

    fun getString(@StringRes id: Int): String = resources.getString(id)

    fun getStringArray(@ArrayRes id: Int): Array<String> = resources.getStringArray(id)
}
