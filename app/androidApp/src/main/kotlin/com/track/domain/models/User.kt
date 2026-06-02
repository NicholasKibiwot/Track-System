package com.track.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "", // "SUPER_ADMIN", "STAFF", "DRIVER", "CUSTOMER"
    val isActive: Boolean = true,
    val phone: String = "",
    val createdAt: Timestamp = Timestamp.now(),
)
