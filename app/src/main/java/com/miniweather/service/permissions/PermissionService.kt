package com.miniweather.service.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

class PermissionService @Inject constructor() {

    private lateinit var permissionCompletable: CompletableDeferred<Boolean>
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>

    fun registerForPermissions(fragment: Fragment) {
        activityResultLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                permissionCompletable.complete(it)
            }
    }

    suspend fun requestPermission(context: Context, permission: String): Boolean {
        if (hasPermission(context, permission))
            return true

        permissionCompletable = CompletableDeferred()
        activityResultLauncher.launch(permission)
        return permissionCompletable.await()
    }

    private fun hasPermission(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

}
