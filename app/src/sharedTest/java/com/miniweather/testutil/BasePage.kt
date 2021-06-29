package com.miniweather.testutil

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import java.util.concurrent.TimeUnit
import junit.framework.AssertionFailedError
import org.hamcrest.Matcher
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not

abstract class BasePage(@IdRes layout: Int) {

    init {
        shouldBeVisible(layout)
    }

    protected fun shouldHaveText(@IdRes view: Int, text: String) {
        shouldBeVisible(view)
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
        shouldBeVisible(view)
        onView(withId(view))
            .perform(click())
    }
}

fun <T : BasePage> onPage(page: T, func: T.() -> Unit): T = page.apply(func)

// https://stackoverflow.com/a/56385404/12519442
fun ViewInteraction.waitUntil(matcher: Matcher<View>, timeoutSeconds: Int = 10): ViewInteraction {
    val maxRetries = timeoutSeconds * 10
    for (i in 1..maxRetries) {
        try {
            check(matches(matcher))
            return this
        } catch (e: AssertionFailedError) {
            TimeUnit.MILLISECONDS.sleep(100L)
        }
    }

    check(matches(matcher))
    return this
}
