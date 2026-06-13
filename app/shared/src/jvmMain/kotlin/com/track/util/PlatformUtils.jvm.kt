package com.track.util

import java.util.UUID

actual fun getCurrentTimestamp(): TrackTimestamp {
    val now = System.currentTimeMillis()
    return TrackTimestamp(now / 1000, ((now % 1000) * 1000000).toInt())
}

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

actual fun randomUUID(): String = UUID.randomUUID().toString()

actual fun logError(tag: String, message: String, throwable: Throwable?) {
    System.err.println("[$tag] $message")
    throwable?.printStackTrace()
}

actual fun logInfo(tag: String, message: String) {
    println("[$tag] $message")
}
