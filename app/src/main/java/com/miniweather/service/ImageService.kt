package com.miniweather.service

import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageService {

    fun loadImage(view: ImageView, url: String) {
        if (url.isNotBlank()) {
            Glide.with(view).load(url).into(view)
        }
    }
}
