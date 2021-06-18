package com.miniweather.testutil

import android.os.Bundle
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import com.miniweather.R

object FragmentLauncher {

    fun <T : Fragment> launchFragment(
        clazz: Class<T>,
        fragment: T,
        fragmentArgs: Bundle?,
        navController: TestNavHostController,
        @NavigationRes navGraph: Int
    ): FragmentScenario<T> =
        FragmentScenario.launchInContainer(
            fragmentClass = clazz,
            fragmentArgs = fragmentArgs,
            themeResId = R.style.AppTheme,
            factory = object : FragmentFactory() {
                override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
                    fragment.apply { setNavController(navGraph, navController) }
            }
        )

    // https://developer.android.com/guide/navigation/navigation-testing#test_navigationui_with_fragmentscenario
    private fun Fragment.setNavController(
        @NavigationRes navGraph: Int,
        navController: TestNavHostController
    ) {
        viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
            if (viewLifecycleOwner != null) {
                navController.setGraph(navGraph)
                Navigation.setViewNavController(requireView(), navController)
            }
        }
    }
}
