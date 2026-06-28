package com.track.data.repository

import com.track.models.User
import com.track.models.UserRole

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<UserRole>
    suspend fun register(email: String, password: String, name: String, phone: String): Result<User>
    suspend fun logout()
    fun getCurrentUserId(): String?
    suspend fun getCurrentUserData(): User?
    fun isAuthenticated(): Boolean
}
