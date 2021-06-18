package com.miniweather.testutil

import android.os.Bundle
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.testing.TestNavHostController
import com.miniweather.R

abstract class BaseFragmentTest<T : Fragment>(private val clazz: Class<T>) :
    BaseInstrumentedTest() {

    protected lateinit var scenario: FragmentScenario<T>
    protected val navController = TestNavHostController(application)

    fun launchFragment(
        fragment: T,
        fragmentArgs: Bundle? = null,
        @NavigationRes navGraph: Int = R.navigation.nav_graph_main
    ) {
        scenario = FragmentLauncher.launchFragment(
            clazz,
            fragment,
            fragmentArgs,
            navController,
            navGraph
        )
    }
}
