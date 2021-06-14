package com.miniweather.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.miniweather.app.BaseApplication
import com.miniweather.di.AppComponent

abstract class BaseFragment<V : BaseContract.View, P : BaseContract.Presenter<V>, B : ViewBinding> :
    Fragment(),
    BaseContract.View {

    abstract val presenter: P

    private var _binding: B? = null
    protected val binding: B
        get() = _binding ?: throw IllegalStateException("Cannot reference view after onDestroyView")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectDependencies()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindView(inflater, container)
        return binding.root
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this as V)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        _binding = null
        super.onDestroyView()
    }

    abstract fun injectDependencies()

    abstract fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): B

}

val BaseFragment<*, *, *>.injector: AppComponent
    get() = (requireActivity().application as BaseApplication).appComponent
