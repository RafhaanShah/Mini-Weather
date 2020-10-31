package com.miniweather.pages

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not

abstract class BasePage {

    abstract fun shouldBeDisplayed()

    fun shouldHaveText(@IdRes view: Int, text: String) {
        onView(withId(view))
            .check(matches(withText(containsString(text))))
    }

    fun shouldBeVisible(@IdRes view: Int) {
        onView(withId(view))
            .check(matches(isDisplayed()))
    }

    fun shouldNotBeVisible(@IdRes view: Int) {
        onView(withId(view))
            .check(matches(not(isDisplayed())))
    }

    fun performClick(@IdRes view: Int) {
        onView(withId(view))
            .perform(click())
    }

}

fun <T : BasePage> onPage(page: T, func: T.() -> Unit): T = page.apply(func)
