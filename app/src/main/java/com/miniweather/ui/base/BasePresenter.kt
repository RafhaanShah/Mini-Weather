package com.miniweather.ui.base

import androidx.annotation.CallSuper

abstract class BasePresenter<V : BaseContract.View> : BaseContract.Presenter<V> {

    private var _view: V? = null
    protected val view: V
        get() = _view ?: throw IllegalStateException("Cannot access view while detached")

    @CallSuper
    override fun onAttachView(view: V) {
        check(_view == null) { "View is already attached to Presenter" }
        _view = view
    }

    @CallSuper
    override fun onDetachView() {
        checkNotNull(_view) { "View is already detached from Presenter" }
        _view = null
    }

}
