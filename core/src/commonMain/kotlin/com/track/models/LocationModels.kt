package com.track.models

import com.track.util.TrackTimestamp
import kotlinx.serialization.Serializable

@Serializable
data class GeoLocation(
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracyMeters: Double = 0.0,
    val address: String = "",
    val timestamp: TrackTimestamp = TrackTimestamp()
)

@Serializable
data class TrackingLocation(
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

@Serializable
data class TrackingRecord(
    val orderId: String = "",
    val currentLocation: TrackingLocation? = null,
    val driverId: String? = null,
    val locationHistory: List<TrackingLocation> = emptyList()
)
