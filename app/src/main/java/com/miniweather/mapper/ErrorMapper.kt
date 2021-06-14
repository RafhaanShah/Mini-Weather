package com.miniweather.mapper

import com.miniweather.R
import retrofit2.HttpException
import java.io.IOException

object ErrorMapper {

    fun mapNetworkException(t: Throwable): Int {
        val err = when (t) {
            is IOException -> ErrorType.NETWORK_IO
            is HttpException -> ErrorType.NETWORK_SERVER
            else -> ErrorType.GENERIC
        }
        return mapError(err)
    }

//    fun mapException(t: Throwable): Int {
//        val err = when (t) {
//            else -> ErrorType.GENERIC
//        }
//        return mapError(err)
//    }

    fun mapError(errorType: ErrorType): Int {
        return when (errorType) {
            ErrorType.NETWORK_SERVER -> R.string.error_network_server
            ErrorType.NETWORK_IO -> R.string.error_network_server
            ErrorType.LOCATION_PERMISSION -> R.string.error_location_permission
            ErrorType.LOCATION_TIMEOUT -> R.string.error_location_timeout
            ErrorType.GENERIC -> R.string.error_generic
        }
    }

}

enum class ErrorType {
    NETWORK_SERVER,
    NETWORK_IO,
    LOCATION_PERMISSION,
    LOCATION_TIMEOUT,
    GENERIC
}
