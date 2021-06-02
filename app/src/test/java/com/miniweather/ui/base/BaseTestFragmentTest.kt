package com.miniweather.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.miniweather.R
import com.miniweather.databinding.FragmentTestBinding
import com.miniweather.testutil.BaseFragmentTest
import com.miniweather.testutil.BasePage
import com.miniweather.testutil.onPage
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

@ExperimentalCoroutinesApi
class BaseTestFragmentTest : BaseFragmentTest<TestFragment>(TestFragment::class.java) {

    @Mock
    private lateinit var mockPresenter: FakeContract.Presenter

    @Before
    fun setup() {
        launchFragment(TestFragment().apply {
            this.presenter = mockPresenter
        })
    }

    @Test
    fun whenAttached_injectsDependencies() {
        scenario.onFragment { fragment ->
            assertTrue(fragment.dependenciesInjected)
        }
    }

    @Test
    fun whenCreateView_inflatesLayout() {
        scenario.onFragment { fragment ->
            assertTrue(fragment.viewBound)
        }

        onPage(TestPage()) { }
    }

    @Test
    fun whenViewCreated_attachesToPresenter() {
        scenario.onFragment { fragment ->
            verify(mockPresenter).onAttachView(fragment)
        }
    }

    @Test
    fun whenViewDestroyed_detachesFromPresenter() {
        scenario.moveToState(Lifecycle.State.DESTROYED)
        verify(mockPresenter).onDetachView()
    }

}

class TestFragment : BaseFragment<FakeContract.View, FakeContract.Presenter>(),
    FakeContract.View {

    var dependenciesInjected = false
    var viewBound = false

    override lateinit var presenter: FakeContract.Presenter

    override lateinit var binding: FragmentTestBinding

    override fun injectDependencies() {
        dependenciesInjected = true
    }

    override fun bindView(inflater: LayoutInflater, container: ViewGroup?) {
        viewBound = true
        binding = FragmentTestBinding.inflate(inflater, container, false)
    }

}

interface FakeContract : BaseContract {

    interface View : BaseContract.View

    interface Presenter : BaseContract.Presenter<View>

}

class TestPage : BasePage(R.id.test_layout)
