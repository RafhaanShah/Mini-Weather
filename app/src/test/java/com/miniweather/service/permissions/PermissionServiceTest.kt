package com.miniweather.service.permissions

import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import com.miniweather.testutil.BaseInstrumentedTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PermissionServiceTest : BaseInstrumentedTest() {

    private lateinit var permissionService: PermissionService

    private var permissionStatus: Boolean? = null

    private val fakeRegistry: ActivityResultRegistry = object : ActivityResultRegistry() {
        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?
        ) {
            assertTrue(contract is ActivityResultContracts.RequestPermission)
            dispatchResult(requestCode, permissionStatus)
        }
    }

    private val fakePermission = "fake.permission"

    @Before
    fun setUp() {
        permissionService = PermissionService()
        permissionStatus = null
    }

    @Test
    fun whenRequestPermission_andPermissionGranted_returnsTrue() = runBlockingTest {
        permissionStatus = true
        val actual = permissionService.requestPermission(fakeRegistry, fakePermission)
        assertTrue(actual)
    }

    @Test
    fun whenRequestPermission_andPermissionDenied_returnsFalse() = runBlockingTest {
        permissionStatus = false
        val actual = permissionService.requestPermission(fakeRegistry, fakePermission)
        assertFalse(actual)
    }

}
