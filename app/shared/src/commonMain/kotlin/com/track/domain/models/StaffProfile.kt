package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class StaffProfile(
    val id: String = "",
    val name: String = "",
    val role: String = "",
    val isActive: Boolean = true
)
