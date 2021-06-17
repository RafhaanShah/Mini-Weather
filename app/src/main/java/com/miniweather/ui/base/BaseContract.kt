package com.miniweather.ui.base

// Pattern borrowed from https://github.com/nomisRev/AndroidCleanMVP
interface BaseContract {

    interface View

    interface Presenter<V : View> {
        fun onAttachView(view: V)
        fun onDetachView()
    }
}
