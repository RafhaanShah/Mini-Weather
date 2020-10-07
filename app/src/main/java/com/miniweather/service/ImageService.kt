package com.miniweather.service

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.miniweather.R

class ImageService {

    fun loadImage(view: ImageView, url: String) {
        if (url.isNotBlank()) {
            Glide.with(view)
                .load(url)
                .placeholder(R.drawable.ic_reload_alert)
                .into(view)
        }
    }

}
