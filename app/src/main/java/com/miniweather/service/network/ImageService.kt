package com.miniweather.service.network

import android.app.Activity
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.miniweather.R

@GlideModule
class GlideModule : AppGlideModule()

class ImageService {

    fun loadImage(activity: Activity, view: ImageView, url: String, @DrawableRes errorImage: Int = R.drawable.ic_reload_alert) {
        if (url.isNotBlank()) {
            Glide.with(activity)
                .load(url)
                .error(errorImage)
                .into(view)
        }
    }

}
