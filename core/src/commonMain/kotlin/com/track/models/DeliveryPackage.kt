package com.track.models

import kotlinx.serialization.Serializable

/**
 * Mirrors the /packages/{packageId} Firestore document.
 */
@Serializable
data class DeliveryPackage(
    val id: String = "",
    val description: String = "",
    val senderUid: String = "",
    val receiverUid: String = "",
    val assignedVehicleId: String = "",
    val status: String = "pending",   // "pending" | "in_transit" | "delivered"
    val currentLatitude: Double = 0.0,
    val currentLongitude: Double = 0.0,
    val updatedAt: Long = 0L
)
