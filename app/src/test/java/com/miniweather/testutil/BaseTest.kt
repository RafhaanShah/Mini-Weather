package com.miniweather.testutil

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miniweather.R
import com.miniweather.app.TestApplication
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

abstract class BaseTest {

    @Before
    fun setupMocks() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }
}

@Config(
    application = TestApplication::class,
    sdk = [Build.VERSION_CODES.R]
)
@RunWith(AndroidJUnit4::class)
abstract class BaseInstrumentedTest : BaseTest() {

    protected val application: TestApplication = ApplicationProvider.getApplicationContext()

    protected val shadowApplication: ShadowApplication
        by lazy { Shadows.shadowOf(application as Application) }
}

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
