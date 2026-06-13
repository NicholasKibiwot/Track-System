package com.track.util

import kotlin.js.Date
import kotlin.random.Random

actual fun getCurrentTimestamp(): TrackTimestamp {
    val now = Date.now().toLong()
    return TrackTimestamp(now / 1000, ((now % 1000) * 1000000).toInt())
}

actual fun getCurrentTimeMillis(): Long = Date.now().toLong()

actual fun randomUUID(): String {
    val chars = "0123456789abcdef"
    return buildString {
        repeat(8) { append(chars[Random.nextInt(16)]) }
        append('-')
        repeat(4) { append(chars[Random.nextInt(16)]) }
        append("-4")
        repeat(3) { append(chars[Random.nextInt(16)]) }
        append('-')
        append(chars[Random.nextInt(4) + 8])
        repeat(3) { append(chars[Random.nextInt(16)]) }
        append('-')
        repeat(12) { append(chars[Random.nextInt(16)]) }
    }
}

actual fun logError(tag: String, message: String, throwable: Throwable?) {
    console.error("[$tag] $message", throwable)
}

actual fun logInfo(tag: String, message: String) {
    console.log("[$tag] $message")
}
