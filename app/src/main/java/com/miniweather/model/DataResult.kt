package com.miniweather.model

sealed class DataResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : DataResult<T>()
    data class Failure(val exception: Exception) : DataResult<Nothing>()
}
