package com.miniweather.ui.base

import com.miniweather.testutil.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BaseTestPresenterTest : BaseTest() {

    private val fakeView = FakeView()

    private lateinit var testPresenter: TestPresenter

    @Before
    fun setup() {
        testPresenter = TestPresenter()
    }

    @Test
    fun whenAttached_viewIsAccessible() {
        testPresenter.onAttachView(fakeView)

        val actual = testPresenter.getView()

        assertEquals(fakeView, actual)
    }

    @Test(expected = IllegalStateException::class)
    fun whenDetached_viewIsNotAccessible() {
        testPresenter.onAttachView(fakeView)
        testPresenter.onDetachView()

        testPresenter.getView()
    }

    @Test(expected = IllegalStateException::class)
    fun whenAttached_cannotAttachAgain() {
        testPresenter.onAttachView(fakeView)

        testPresenter.onAttachView(fakeView)
    }

    @Test(expected = IllegalStateException::class)
    fun whenDetached_cannotDetachAgain() {
        testPresenter.onAttachView(fakeView)
        testPresenter.onDetachView()

        testPresenter.onDetachView()
    }

}

class TestPresenter: BasePresenter<FakeView>() {

    fun getView() = view

}

class FakeView: BaseContract.View
