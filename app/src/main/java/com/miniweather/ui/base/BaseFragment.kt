package com.miniweather.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.miniweather.app.BaseApplication

abstract class BaseFragment<V : BaseContract.View, P : BaseContract.Presenter<V>> :
    Fragment(),
    BaseContract.View {

    abstract val presenter: P

    protected abstract val binding: ViewBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectDependencies()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindView(inflater, container)
        return binding.root
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this as V)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }

    abstract fun injectDependencies()

    abstract fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup?
    )

}

val BaseFragment<*, *>.injector
    get() = (requireActivity().application as BaseApplication).getAppComponent()
