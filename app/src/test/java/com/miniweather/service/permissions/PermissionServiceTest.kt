package com.miniweather.service.permissions

import android.Manifest
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.miniweather.testutil.BaseInstrumentedTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock

@ExperimentalCoroutinesApi
class PermissionServiceTest : BaseInstrumentedTest() {

    @Mock
    private lateinit var mockFragment: Fragment

    @Mock
    private lateinit var mockActivityResultLauncher: ActivityResultLauncher<String>

    @Captor
    private lateinit var activityResultCaptor: ArgumentCaptor<ActivityResultCallback<Boolean>>

    private lateinit var permissionService: PermissionService

    @Before
    fun setUp() {
        whenever(
            mockFragment.registerForActivityResult<String, Boolean>(
                any(),
                activityResultCaptor.capture()
            )
        )
            .thenReturn(mockActivityResultLauncher)
        permissionService = PermissionService()
        permissionService.registerForPermissions(mockFragment)
    }

    @Test
    fun whenRequestPermission_andPermissionHasNotBeenGranted_requestsPermission() =
        runBlockingTest {
            val expected = true
            whenever(mockActivityResultLauncher.launch(any())).then {
                activityResultCaptor.value.onActivityResult(expected)
            }

            val actual = permissionService.requestPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            assertEquals(expected, actual)
        }

    @Test
    fun whenRequestPermission_andPermissionAlreadyGranted_returnsTrue() = runBlockingTest {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        val actual = permissionService.requestPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        assertTrue(actual)
    }

    @Test(expected = IllegalStateException::class)
    fun whenRequestPermission_withoutRegistering_throwsError() = runBlockingTest {
        val unRegisteredPermissionsService = PermissionService()
        unRegisteredPermissionsService.requestPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

}
