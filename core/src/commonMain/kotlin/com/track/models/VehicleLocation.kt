package com.track.models

import kotlinx.serialization.Serializable

/**
 * Mirrors the /vehicles/{vehicleId}/locations/{locationId} sub-collection.
 * Each document is one GPS ping from the vehicle.
 */
@Serializable
data class VehicleLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val speed: Double = 0.0,
    val heading: Double = 0.0,
    val timestamp: Long = 0L
)
