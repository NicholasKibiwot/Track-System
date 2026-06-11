package com.track.domain.models

data class TrackingRecord(
    val orderId: String = "",
    val currentLocation: TrackingLocation? = null,
    val driverId: String = "",
    val lastUpdatedMillis: Long = 0L,
    val locationHistory: List<String> = emptyList(),
)

data class TrackingLocation(
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
)
