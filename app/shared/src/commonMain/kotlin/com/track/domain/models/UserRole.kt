package com.track.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    CUSTOMER,
    DRIVER,
    STAFF,
    ADMIN,
    SUPER_ADMIN
}
