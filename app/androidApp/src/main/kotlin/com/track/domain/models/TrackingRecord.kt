package com.track.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class TrackingLocation(
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
)

// Represents a document in the "tracking" collection (keyed by orderId)
data class TrackingRecord(
    @DocumentId val orderId: String = "",
    val currentLocation: TrackingLocation = TrackingLocation(),
    val driverId: String = "",
    val lastUpdated: Timestamp = Timestamp.now(),
    val locationHistory: List<String> = emptyList(),
)
