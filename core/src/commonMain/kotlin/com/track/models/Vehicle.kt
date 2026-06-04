package com.track.models

import kotlinx.serialization.Serializable

/**
 * Mirrors the /vehicles/{vehicleId} Firestore document.
 */
@Serializable
data class Vehicle(
    val id: String = "",
    val name: String = "",
    val plateNumber: String = "",
    val driverUid: String = "",
    val status: String = "idle",      // "active" | "idle" | "offline"
    val currentLatitude: Double = 0.0,
    val currentLongitude: Double = 0.0,
    val lastUpdated: Long = 0L
)
