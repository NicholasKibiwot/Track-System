package com.track.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val stock: Int = 0,
    val branch: String = "", // Branch where item is stored
    val category: String = "",
    val isActive: Boolean = true,
    val addedBy: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(), // Store as Hex strings
    val isFlashSale: Boolean = false,
    val discountPercent: Int = 0
)
