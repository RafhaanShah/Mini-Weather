package com.miniweather.service.permissions

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class PermissionService @Inject constructor() {

    private val nextRequestCode = AtomicInteger()
    private var permissionRequest: ActivityResultLauncher<String>? = null
    private lateinit var permissionCompletable: CompletableDeferred<Boolean>

    suspend fun requestPermission(registry: ActivityResultRegistry, permission: String): Boolean {
        permissionRequest = getRequest(registry)
        permissionCompletable = CompletableDeferred()
        permissionRequest?.launch(permission)
        return permissionCompletable.await()
    }

    fun unregister() {
        permissionRequest?.unregister()
    }

    private fun getRequest(registry: ActivityResultRegistry): ActivityResultLauncher<String> {
        unregister()
        return registry.register(
            "permission_request#" + nextRequestCode.getAndIncrement(),
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            permissionCompletable.complete(granted)
        }
    }

}
