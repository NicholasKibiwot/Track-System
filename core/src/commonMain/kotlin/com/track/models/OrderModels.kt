package com.track.models

import com.track.util.TrackTimestamp
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
    DELAYED,
    RETURNED
}

@Serializable
enum class PaymentStatus {
    PENDING,
    PAID,
    PARTIAL,
    FAILED,
    REFUNDED
}

@Serializable
enum class PaymentMethod {
    CASH,
    M_PESA,
    MOMO,
    CARD,
    BANK_TRANSFER,
    CREDIT
}

@Serializable
data class OrderItem(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = quantity * unitPrice,
    val imageUrl: String? = null,
    val branch: String = "",
    val machineType: MachineType? = null
)

@Serializable
data class Order(
    val id: String = "",
    val orderNumber: String = "",
    val trackingNumber: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val currency: String = "KES",
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentMethod: PaymentMethod? = null,
    val deliveryType: String = "COMPANY", // "COMPANY", "OUTSOURCED", "PICKUP"
    val driverId: String? = null,
    val driverName: String? = null,
    val origin: String = "",
    val destination: String = "",
    val shippingAddress: String = "",
    val currentLocation: GeoLocation? = null,
    val locationHistory: List<GeoLocation> = emptyList(),
    val createdAt: TrackTimestamp = TrackTimestamp(),
    val updatedAt: TrackTimestamp = TrackTimestamp(),
    val notes: String = ""
)
