package com.track.util

import platform.Foundation.NSUUID
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun getCurrentTimestamp(): TrackTimestamp {
    val now = NSDate().timeIntervalSince1970
    return TrackTimestamp(now.toLong(), ((now % 1.0) * 1000000000).toInt())
}

actual fun getCurrentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

actual fun randomUUID(): String = NSUUID().UUIDString()

actual fun logError(tag: String, message: String, throwable: Throwable?) {
    println("ERROR: [$tag] $message")
    throwable?.printStackTrace()
}

actual fun logInfo(tag: String, message: String) {
    println("INFO: [$tag] $message")
}
