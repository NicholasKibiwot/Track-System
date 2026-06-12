package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String = "",
    val customerId: String = "",
    val driverId: String? = null,
    val items: List<String> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val deliveryAddress: String = "",
    val currentLocation: GeoLocation? = null,
    val createdAt: Long = 0L
)
