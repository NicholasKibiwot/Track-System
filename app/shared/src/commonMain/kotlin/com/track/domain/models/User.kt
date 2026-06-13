package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val displayName: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val phone: String = "",
    val phoneNumber: String = "",
    val branch: String = "",
    val isActive: Boolean = true,
    val isOnline: Boolean = false,
    val shippingAddress: String = "",
    val dob: String = "",
    val country: String = ""
)
