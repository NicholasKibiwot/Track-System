package com.track.domain.models

import com.google.firebase.firestore.DocumentId

data class OrderItem(
    @DocumentId val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val imageUrl: String? = null,
    val machineType: MachineType? = null, // For printing machine logistics
)
