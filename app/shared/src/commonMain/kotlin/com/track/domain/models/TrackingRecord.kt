package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class TrackingRecord(
    val orderId: String = "",
    val locations: List<TrackingLocation> = emptyList(),
    val lastUpdated: Long = 0L
)
