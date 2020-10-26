package com.miniweather.testutil

import org.junit.Rule
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

abstract class BaseTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

}
