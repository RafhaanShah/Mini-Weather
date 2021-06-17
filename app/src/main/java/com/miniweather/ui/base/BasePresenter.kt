package com.miniweather.ui.base

import androidx.annotation.CallSuper
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

abstract class BasePresenter<V : BaseContract.View> :
    BaseContract.Presenter<V>, CoroutineScope {

    private val job = Job()
    private var _view: V? = null

    protected val view: V
        get() = _view ?: throw IllegalStateException("Cannot access view while detached")

    protected abstract val dispatcher: CoroutineDispatcher

    override val coroutineContext: CoroutineContext
        get() = job + dispatcher

    @CallSuper
    override fun onAttachView(view: V) {
        check(_view == null) { "View is already attached to Presenter" }
        _view = view
    }

    @CallSuper
    override fun onDetachView() {
        job.cancel(CancellationException("Presenter has been detached from the view"))
        checkNotNull(_view) { "View is already detached from Presenter" }
        _view = null
    }
}
