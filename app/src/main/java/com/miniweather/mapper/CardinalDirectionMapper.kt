package com.miniweather.mapper

import kotlin.math.floor

object CardinalDirectionMapper {

    fun map(bearing: Double): Int = (floor(((bearing - 22.5) % 360) / 45).toInt() + 1) % 8

}
