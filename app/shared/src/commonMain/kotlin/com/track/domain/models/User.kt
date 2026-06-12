package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val phone: String = "",
    val isActive: Boolean = true
)
