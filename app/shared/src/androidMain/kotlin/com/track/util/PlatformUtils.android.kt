package com.track.util

import android.util.Log
import java.util.UUID

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

actual fun randomUUID(): String = UUID.randomUUID().toString()

actual fun logError(tag: String, message: String, throwable: Throwable?) {
    Log.e(tag, message, throwable)
}

actual fun logInfo(tag: String, message: String) {
    Log.i(tag, message)
}
