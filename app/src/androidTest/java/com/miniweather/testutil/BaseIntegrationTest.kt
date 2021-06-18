package com.miniweather.testutil

import android.os.Bundle
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miniweather.R
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseIntegrationTest<T : Fragment>(private val clazz: Class<T>) : BaseAndroidTest() {

    protected val navController = TestNavHostController(appContext)

    fun launchFragment(
        fragmentArgs: Bundle? = null,
        @NavigationRes navGraph: Int = R.navigation.nav_graph_main
    ) {
        FragmentLauncher.launchFragment(
            clazz,
            clazz.newInstance(),
            fragmentArgs,
            navController,
            navGraph
        )
    }
}
