package com.miniweather.testutil

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

abstract class BasePage(@IdRes layout: Int) {

    init {
        shouldBeVisible(layout)
    }

    protected fun shouldHaveText(@IdRes view: Int, text: String) {
        onView(withId(view))
            .check(matches(withText(containsString(text))))
    }

    protected fun shouldBeVisible(@IdRes view: Int) {
        onView(withId(view))
            .waitUntil(isDisplayed())
    }

    protected fun shouldNotBeVisible(@IdRes view: Int) {
        onView(withId(view))
            .waitUntil(not(isDisplayed()))
    }

    protected fun performClick(@IdRes view: Int) {
        onView(withId(view))
            .perform(click())
    }

}

fun <T : BasePage> onPage(page: T, func: T.() -> Unit): T = page.apply(func)

private const val waitTimeMillis = 100L
private const val maxRetries = 100

// https://stackoverflow.com/a/56385404/12519442
fun ViewInteraction.waitUntil(matcher: Matcher<View>): ViewInteraction {
    for (i in 1..maxRetries) {
        try {
            check(matches(matcher))
            return this
        } catch (e: AssertionFailedError) {
            TimeUnit.MILLISECONDS.sleep(waitTimeMillis)
        }
    }

    check(matches(matcher))
    return this
}