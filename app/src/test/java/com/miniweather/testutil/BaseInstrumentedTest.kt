package com.miniweather.testutil

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miniweather.app.TestApplication
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

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
