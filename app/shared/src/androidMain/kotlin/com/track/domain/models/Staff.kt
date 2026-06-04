package com.track.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Staff(
    @DocumentId val id: String = "",
    val userId: String = "",
    val employeeId: String = "",
    val department: String = "",
    val office: String = "",
    val isActive: Boolean = true,
    val hiredBy: String = "",
    val createdAt: Timestamp = Timestamp.now(),
)
