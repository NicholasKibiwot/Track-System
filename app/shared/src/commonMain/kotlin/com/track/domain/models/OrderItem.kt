package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val branch: String = "",
    val machineType: MachineType? = null,
    val imageUrl: String? = null
)

