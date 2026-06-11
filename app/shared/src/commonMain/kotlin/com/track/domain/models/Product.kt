package com.track.domain.models

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val stock: Int = 0,
    val branch: String = "",
    val category: String = "",
    val isActive: Boolean = true,
    val addedBy: String = "",
    val createdAtMillis: Long = 0L,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val isFlashSale: Boolean = false,
    val discountPercent: Int = 0
)
