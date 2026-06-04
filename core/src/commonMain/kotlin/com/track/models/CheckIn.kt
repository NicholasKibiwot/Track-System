package com.track.models

import kotlinx.serialization.Serializable

/**
 * Mirrors the /checkIns/{checkInId} Firestore document.
 */
@Serializable
data class CheckIn(
    val userUid: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = "",
    val timestamp: Long = 0L,
    val type: String = "manual"       // "manual" | "auto"
)
