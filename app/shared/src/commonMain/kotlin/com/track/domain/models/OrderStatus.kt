package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {
    PENDING,
    ACCEPTED,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}
