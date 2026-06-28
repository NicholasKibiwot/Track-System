package com.track.util

expect fun getCurrentTimeMillis(): Long

expect fun randomUUID(): String

expect fun logError(tag: String, message: String, throwable: Throwable? = null)
expect fun logInfo(tag: String, message: String)
