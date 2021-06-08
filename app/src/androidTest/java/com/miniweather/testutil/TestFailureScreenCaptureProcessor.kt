package com.miniweather.testutil

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.ScreenCapture
import com.miniweather.BuildConfig
import java.io.File
import java.io.IOException

class TestFailureScreenCaptureProcessor(
    private val contentResolver: ContentResolver,
    parentFolder: String
) :
    BasicScreenCaptureProcessor() {

    private val screenshotFolder: String

    init {
        // /storage/emulated/0/Pictures/application_id.build_type/full_class_name/test_name.png
        var screenshotFolderName = BuildConfig.APPLICATION_ID
        if (!BuildConfig.APPLICATION_ID.endsWith(BuildConfig.BUILD_TYPE))
            screenshotFolderName = screenshotFolderName.plus(".${BuildConfig.BUILD_TYPE}")

        screenshotFolder = "${screenshotFolderName}/$parentFolder"
        mDefaultScreenshotPath = File(
            mDefaultScreenshotPath.parentFile, screenshotFolder
        )
    }

    // https://commonsware.com/blog/2019/12/21/scoped-storage-stories-storing-mediastore.html
    // https://proandroiddev.com/working-with-scoped-storage-8a7e7cafea3
    // https://stackoverflow.com/a/66817176
    override fun process(capture: ScreenCapture): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            saveImageWithMediaStore(capture)
        else super.process(capture)
    }

    private fun saveImageWithMediaStore(capture: ScreenCapture): String {
        val format = capture.format.toString().lowercase()
        val filename = "${capture.name}.${format}"

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/${format}")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${screenshotFolder}/")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val imageUri = contentResolver.insert(collection, values)
            ?: throw IOException("Content Resolver returned null URI")

        contentResolver.openOutputStream(imageUri).use { out ->
            capture.bitmap.compress(capture.format, 100, out)
        }

        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        contentResolver.update(imageUri, values, null, null)

        return filename
    }

}
