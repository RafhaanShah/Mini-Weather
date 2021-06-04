package com.miniweather.testutil

import android.util.Log
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class TestFailureScreenshotRule : TestWatcher() {

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
                .process(setOf(ScreenCaptureProcessor(parentFolder)))
        } catch (e: Exception) {
            Log.e("TestFailureScreenshotRule", "Error taking screenshot: ", e)
        }
    }

}
