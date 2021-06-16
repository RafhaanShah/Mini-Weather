package com.miniweather.mapper

import androidx.annotation.StringRes
import com.miniweather.R
import com.miniweather.provider.ResourceProvider
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ErrorMapper @Inject constructor(private val resourceProvider: ResourceProvider) {

    fun mapNetworkException(t: Throwable): String {
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

    fun mapError(errorType: ErrorType): String {
        return when (errorType) {
            ErrorType.NETWORK_SERVER -> getString(R.string.error_network_server)
            ErrorType.NETWORK_IO -> getString(R.string.error_network_server)
            ErrorType.LOCATION_PERMISSION -> getString(R.string.error_location_permission)
            ErrorType.LOCATION_TIMEOUT -> getString(R.string.error_location_timeout)
            ErrorType.GENERIC -> getString(R.string.error_generic)
        }
    }

    private fun getString(@StringRes id: Int): String = resourceProvider.getString(id)

}

enum class ErrorType {
    NETWORK_SERVER,
    NETWORK_IO,
    LOCATION_PERMISSION,
    LOCATION_TIMEOUT,
    GENERIC
}
