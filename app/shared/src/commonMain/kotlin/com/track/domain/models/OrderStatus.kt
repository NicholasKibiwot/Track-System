package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {
    PENDING,
    ACCEPTED,
    PROCESSING,
    SHIPPED,
    INTRANSIT,
    DELIVERED,
    CANCELLED,
    DELAYED
}

