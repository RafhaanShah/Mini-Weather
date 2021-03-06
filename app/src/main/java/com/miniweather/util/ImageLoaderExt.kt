package com.miniweather.util

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.ImageLoader
import coil.request.ImageRequest

fun ImageLoader.load(
    view: ImageView,
    url: String,
    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null
) {
    enqueue(
        ImageRequest.Builder(view.context).data(url).target(view).apply {
            placeholder?.let { placeholder(it) }
            error?.let { error(it) }
        }.build()
    )
}
