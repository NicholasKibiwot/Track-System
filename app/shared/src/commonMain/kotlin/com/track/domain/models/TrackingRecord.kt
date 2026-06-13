package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class TrackingRecord(
    val orderId: String = "",
    val currentLocation: TrackingLocation? = null,
    val driverId: String = "",
    val locationHistory: List<TrackingLocation> = emptyList()
)

