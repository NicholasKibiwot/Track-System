package com.track.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class User(
    @DocumentId val id: String = "",
    val email: String = "",
    @get:PropertyName("displayName") @set:PropertyName("displayName") var name: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    @get:PropertyName("phoneNumber") @set:PropertyName("phoneNumber") var phone: String = "",
    val address: String = "",
    val shippingAddress: String = "",
    val dob: String = "",
    val country: String = "",
    val profilePictureUrl: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val isActive: Boolean = true,
    val isOnline: Boolean = false,
)
