package com.track.models

import kotlinx.serialization.Serializable

@Serializable
data class Shipment(
    val id: String = "",
    val orderId: String = "",
    val trackingNumber: String = "",
    val carrier: String = "YheCutMedia Logistics",
    val status: String = "PENDING",
    val estimatedDelivery: Long? = null,
    val actualDelivery: Long? = null,
    val origin: String = "",
    val destination: String = "",
    val weightKg: Double = 0.0,
    val dimensions: String = "", // LxWxH
    val createdAt: Long = 0L
)

@Serializable
data class InventoryLog(
    val id: String = "",
    val productId: String = "",
    val changeAmount: Int = 0,
    val previousStock: Int = 0,
    val currentStock: Int = 0,
    val reason: String = "", // SALE, RESTOCK, ADJUSTMENT, RETURN
    val branch: String = "",
    val recordedBy: String = "",
    val createdAt: Long = 0L
)
