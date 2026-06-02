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
    val category: String = "",
    val isActive: Boolean = true,
    val addedBy: String = "",
    val createdAt: Timestamp = Timestamp.now(),
)
