package com.track.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

// Represents a document in the "staff" collection
data class StaffProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val employeeId: String = "",
    val department: String = "",
    val office: String = "",
    val hiredBy: String = "",
    val isActive: Boolean = true,
    val createdAt: Timestamp = Timestamp.now(),
)
