package com.miniweather.ui.base

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.miniweather.app.BaseApplication
import kotlinx.coroutines.CompletableDeferred

abstract class BaseActivity<V : BaseContract.View, P : BaseContract.Presenter<V>> : AppCompatActivity(),
                                                                                    BaseContract.View {

    abstract val presenter: P

    protected abstract val binding: ViewBinding

    private lateinit var permissionCompletable: CompletableDeferred<Boolean>
    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionCompletable.complete(granted)
        }

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
        permissionCompletable = CompletableDeferred()
        permissionRequest.launch(permission)
        return permissionCompletable.await()
    }

}

val Activity.injector get() = (application as BaseApplication).getAppComponent()
