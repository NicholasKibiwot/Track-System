package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class StaffProfile(
    val id: String = "",
    val userId: String = "",
    val employeeId: String = "",
    val department: String = "",
    val office: String = "",
    val hiredBy: String = "",
    val isActive: Boolean = true
)

