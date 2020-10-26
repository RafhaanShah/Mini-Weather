package com.miniweather.testutil

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miniweather.app.TestApplication
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = TestApplication::class)
@RunWith(AndroidJUnit4::class)
abstract class BaseInstrumentedTest : BaseTest() {

    protected val context: Context = ApplicationProvider.getApplicationContext()

}
