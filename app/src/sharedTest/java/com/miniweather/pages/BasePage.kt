package com.miniweather.pages

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import junit.framework.AssertionFailedError
import org.hamcrest.Matcher
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

abstract class BasePage {

    abstract fun shouldBeDisplayed()

    protected fun shouldHaveText(@IdRes view: Int, text: String) {
        onView(withId(view))
            .check(matches(withText(containsString(text))))
    }

    protected fun shouldBeVisible(@IdRes view: Int) {
        onView(withId(view))
            .waitUntil(matcher = isDisplayed())
    }

    protected fun shouldNotBeVisible(@IdRes view: Int) {
        onView(withId(view))
            .waitUntil(matcher = not(isDisplayed()))
    }

    protected fun performClick(@IdRes view: Int) {
        onView(withId(view))
            .perform(click())
    }

}

fun <T : BasePage> onPage(page: T, func: T.() -> Unit): T = page.apply(func)

// https://stackoverflow.com/a/56385404/12519442
fun ViewInteraction.waitUntil(timeout: Long = TimeUnit.SECONDS.toMillis(5), matcher: Matcher<View>): ViewInteraction {
    val startTime = System.currentTimeMillis()
    val endTime = startTime + timeout

    do {
        try {
            check(matches(matcher))
            return this
        } catch (e: AssertionFailedError) {
            Thread.sleep(100)
        }
    } while (System.currentTimeMillis() < endTime)

    throw TimeoutException("Timed out waiting for view to be visible")
}
