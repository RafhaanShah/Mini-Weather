package com.miniweather.service

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.miniweather.R

class ImageService {

    fun loadImage(view: ImageView, url: String, @DrawableRes errorImage: Int = R.drawable.ic_reload_alert) {
        if (url.isNotBlank()) {
            Glide.with(view)
                .load(url)
                .error(errorImage)
                .into(view)
        }
    }

}
