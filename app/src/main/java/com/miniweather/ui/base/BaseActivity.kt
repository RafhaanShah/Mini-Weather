package com.miniweather.ui.base

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.miniweather.app.BaseApplication
import com.miniweather.service.permissions.PermissionService
import javax.inject.Inject

abstract class BaseActivity<V : BaseContract.View, P : BaseContract.Presenter<V>> : AppCompatActivity(),
                                                                                    BaseContract.View {

    @Inject
    lateinit var permissionService: PermissionService

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
        permissionService.unregister()
        super.onDestroy()
    }

    abstract fun injectDependencies()

    abstract fun bindView()

    suspend fun checkAndRequestPermission(permission: String): Boolean {
        if (hasPermission(permission)) {
            return true
        }

        return requestPermission(permission)
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun requestPermission(permission: String): Boolean {
        return permissionService.requestPermission(activityResultRegistry, permission)
    }

}

val Activity.injector get() = (application as BaseApplication).getAppComponent()
