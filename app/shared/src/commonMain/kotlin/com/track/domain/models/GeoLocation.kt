package com.track.domain.models

data class GeoLocation(
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracyMeters: Double = 0.0,
    val timestampMillis: Long = 0L,
    val address: String = "",
)
