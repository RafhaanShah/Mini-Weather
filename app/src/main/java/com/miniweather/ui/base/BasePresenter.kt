package com.miniweather.ui.base

import androidx.annotation.CallSuper

abstract class BasePresenter<V : BaseContract.View> : BaseContract.Presenter<V> {

    protected var view: V? = null

    @CallSuper
    override fun onAttachView(view: V) {
        this.view = view
    }

    @CallSuper
    override fun onDetachView() {
        view = null
    }

}
