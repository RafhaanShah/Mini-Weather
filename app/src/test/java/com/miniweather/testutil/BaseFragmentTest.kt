package com.miniweather.testutil

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import com.miniweather.R

abstract class BaseFragmentTest<T : Fragment>(private val clazz: Class<T>) :
    BaseInstrumentedTest() {

    protected lateinit var scenario: FragmentScenario<T>

    protected fun launchFragment(fragment: T, fragmentArgs: Bundle? = null) {
        scenario = FragmentScenario.launchInContainer(
            fragmentClass = clazz,
            fragmentArgs = fragmentArgs,
            themeResId = R.style.AppTheme,
            factory = object : FragmentFactory() {
                override fun instantiate(classLoader: ClassLoader, className: String) = fragment
            })
    }

}
