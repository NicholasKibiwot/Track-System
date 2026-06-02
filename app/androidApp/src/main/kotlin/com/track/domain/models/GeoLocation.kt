package com.track.domain.models

import com.google.firebase.Timestamp

data class GeoLocation(
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracyMeters: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now(),
    val address: String = "",
)
