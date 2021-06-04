package com.miniweather.testutil

import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import com.miniweather.BuildConfig
import java.io.File

class ScreenCaptureProcessor(parentFolder: String) : BasicScreenCaptureProcessor() {

    init {
        // /storage/emulated/0/Pictures/application_id.build_type/full_class_name/test_name.png
        var screenshotFolderName = BuildConfig.APPLICATION_ID
        if (!BuildConfig.APPLICATION_ID.endsWith(BuildConfig.BUILD_TYPE))
            screenshotFolderName = screenshotFolderName.plus(".${BuildConfig.BUILD_TYPE}")

        mDefaultScreenshotPath = File(
            mDefaultScreenshotPath.parentFile, "${screenshotFolderName}/$parentFolder"
        )
    }

    override fun getFilename(prefix: String) = prefix

}
