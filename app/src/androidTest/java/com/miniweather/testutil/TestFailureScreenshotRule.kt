package com.miniweather.testutil

import android.content.ContentResolver
import android.util.Log
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class TestFailureScreenshotRule(private val contentResolver: ContentResolver) : TestWatcher() {

    override fun failed(e: Throwable?, description: Description) {
        takeScreenshot(
            parentFolder = description.className,
            fileName = description.methodName
        )
    }

    private fun takeScreenshot(parentFolder: String, fileName: String) {
        try {
            Screenshot.capture()
                .setName(fileName)
                .process(setOf(TestFailureScreenCaptureProcessor(contentResolver, parentFolder)))
        } catch (e: Exception) {
            Log.e("TestFailureScreenshotRule", "Error taking screenshot: ", e)
        }
    }
}
