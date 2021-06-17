package com.miniweather.service.network

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.miniweather.R
import javax.inject.Inject

@GlideModule
class GlideModule : AppGlideModule()

class ImageService @Inject constructor() {

    fun loadImage(
        view: ImageView,
        url: String,
        @DrawableRes errorImage: Int = R.drawable.ic_reload_alert
    ) {
        if (url.isNotBlank()) {
            Glide.with(view)
                .load(url)
                .error(errorImage)
                .into(view)
        }
    }
}
