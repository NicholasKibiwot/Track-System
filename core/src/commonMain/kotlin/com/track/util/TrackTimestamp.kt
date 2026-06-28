package com.track.util

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class TrackTimestamp(
    val seconds: Long = 0,
    val nanoseconds: Int = 0
) {
    companion object {
        fun now(): TrackTimestamp {
            val now = Clock.System.now()
            return TrackTimestamp(now.epochSeconds, now.nanosecondsOfSecond)
        }
    }
}
