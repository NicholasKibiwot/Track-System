package com.track.domain.models

import com.track.util.TrackTimestamp
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String = "",
    val trackingNumber: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "",
    val paymentStatus: String = "",
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val deliveryType: String = "",
    val driverId: String? = null,
    val driverName: String? = null,
    val origin: String = "",
    val destination: String = "",
    val currentLocation: GeoLocation? = null,
    val locationHistory: List<GeoLocation> = emptyList(),
    val createdAt: TrackTimestamp = TrackTimestamp(),
    val updatedAt: TrackTimestamp = TrackTimestamp()
)
