package com.track.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.CUSTOMER, // 👈 Must be UserRole, NOT String
    val phone: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val isActive: Boolean = true,
    val isOnline: Boolean = false,
)
