package com.track.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    INTRANSIT,
    DELIVERED,
    DELAYED,
    CANCELLED,
}

data class Order(
    @DocumentId val id: String = "",
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
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
)
