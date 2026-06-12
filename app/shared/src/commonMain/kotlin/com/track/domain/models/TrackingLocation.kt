package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class TrackingLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L
)
