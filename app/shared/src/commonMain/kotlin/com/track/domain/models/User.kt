package com.track.domain.models

data class User(
    val id: String = "",
    val email: String = "",
    var name: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    var phone: String = "",
    val address: String = "",
    val shippingAddress: String = "",
    val dob: String = "",
    val country: String = "",
    val profilePictureUrl: String = "",
    val branch: String = "",
    val createdAtMillis: Long = 0L,
    val isActive: Boolean = true,
    val isOnline: Boolean = false,
)
