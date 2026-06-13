package com.track.util

import kotlinx.serialization.Serializable

@Serializable
data class TrackTimestamp(
    val seconds: Long = 0,
    val nanoseconds: Int = 0
) {
    companion object {
        fun now(): TrackTimestamp = getCurrentTimestamp()
    }
}

expect fun getCurrentTimestamp(): TrackTimestamp

expect fun getCurrentTimeMillis(): Long

expect fun randomUUID(): String

expect fun logError(tag: String, message: String, throwable: Throwable? = null)
expect fun logInfo(tag: String, message: String)
