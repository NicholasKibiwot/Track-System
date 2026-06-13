package com.track.domain.models

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

