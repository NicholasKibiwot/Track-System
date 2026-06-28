package com.track.models

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    CUSTOMER,
    DRIVER,
    STAFF,
    ADMIN,
    SUPER_ADMIN
}

/**
 * Mirrors the /users/{uid} Firestore document.
 * Customers sign in via Google; this document is created on first login.
 */
@Serializable
data class User(
    val uid: String = "",
    val id: String = "", // Alias for uid if needed
    val email: String = "",
    val name: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val phone: String = "",
    val phoneNumber: String = "",
    val branch: String = "",
    val isActive: Boolean = true,
    val isOnline: Boolean = false,
    val shippingAddress: String = "",
    val dob: String = "",
    val country: String = "",
    val createdAt: Long = 0L
)
