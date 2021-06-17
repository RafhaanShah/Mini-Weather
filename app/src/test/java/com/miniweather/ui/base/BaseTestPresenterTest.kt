package com.miniweather.ui.base

import com.google.common.truth.Truth.assertThat
import com.miniweather.testutil.BaseTest
import com.miniweather.testutil.CoroutineTestRule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.isActive
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class BaseTestPresenterTest : BaseTest() {

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    private val fakeView = FakeView()

    private lateinit var testPresenter: TestPresenter

    @Before
    fun setup() {
        testPresenter = TestPresenter(coroutinesTestRule.testDispatcher)
    }

    @Test
    fun whenAttached_viewIsAccessible() {
        testPresenter.onAttachView(fakeView)

        val actual = testPresenter.getView()

        assertThat(actual).isEqualTo(fakeView)
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

    @Test
    fun whenDetached_cancelsCoroutines() {
        testPresenter.onAttachView(fakeView)
        val scope = testPresenter.coroutineContext

        testPresenter.onDetachView()

        assertThat(scope.isActive).isFalse()
    }
}

class TestPresenter(override val dispatcher: CoroutineDispatcher) : BasePresenter<FakeView>() {

    fun getView() = view
}

class FakeView : BaseContract.View
