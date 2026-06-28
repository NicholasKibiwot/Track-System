package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val images: List<com.track.models.ProductImage> = emptyList(),
    val stock: Int = 0,
    val category: String = "",
    val branch: String = "",
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val rating: Double = 0.0,
    val isActive: Boolean = true,
    val addedBy: String = ""
)

