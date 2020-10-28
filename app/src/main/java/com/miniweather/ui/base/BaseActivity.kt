package com.miniweather.ui.base

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.miniweather.app.BaseApplication

abstract class BaseActivity<V : BaseContract.View, P : BaseContract.Presenter<V>> : AppCompatActivity(),
                                                                                    BaseContract.View {

    protected abstract val binding: ViewBinding
    abstract val presenter: P

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)
        bindView()
        presenter.onAttachView(this as V)
    }

    override fun onDestroy() {
        presenter.onDetachView()
        super.onDestroy()
    }

    abstract fun injectDependencies()

    abstract fun bindView()

}

val Activity.injector get() = (application as BaseApplication).getAppComponent()
