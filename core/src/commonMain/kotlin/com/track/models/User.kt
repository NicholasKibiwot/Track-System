package com.track.models

import kotlinx.serialization.Serializable

/**
 * Mirrors the /users/{uid} Firestore document.
 * Customers sign in via Google; this document is created on first login.
 */
@Serializable
data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val phoneNumber: String = "",
    val role: String = "customer",   // "customer" | "driver" | "admin"
    val createdAt: Long = 0L          // epoch millis
)
