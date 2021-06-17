package com.miniweather.testutil

import io.mockk.MockKAnnotations
import org.junit.Before

abstract class BaseTest {

    @Before
    fun setupMocks() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }
}
