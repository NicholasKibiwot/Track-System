package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class TrackingLocation(
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val timestamp: Long = 0L
)

