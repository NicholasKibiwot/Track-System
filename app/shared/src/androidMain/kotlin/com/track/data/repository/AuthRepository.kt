package com.track.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.track.domain.models.User
import com.track.domain.models.UserRole
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
        private val firestoreRepository: FirestoreRepository,
    ) {
        val currentUser: FirebaseUser? get() = firebaseAuth.currentUser
        val currentUserId: String? get() = currentUser?.uid

        suspend fun signIn(
            email: String,
            password: String,
        ): Result<UserRole> {
            return try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val user = result.user ?: return Result.failure(Exception("User is null"))

                // Fetch user document to get role (Fixes "Unresolved reference 'getUser'" & "'role'")
                val userData = firestoreRepository.getUser(user.uid)
                val role = userData?.role ?: UserRole.CUSTOMER

                Result.success(role)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        suspend fun signUp(
            email: String,
            password: String,
            name: String,
            role: UserRole = UserRole.CUSTOMER,
            phone: String = "",
        ): Result<FirebaseUser> {
            return try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user ?: return Result.failure(Exception("User is null"))

                val userData =
                    User(
                        id = user.uid,
                        email = email,
                        name = name,
                        role = role, // Now safely matches the UserRole enum in User.kt
                        phone = phone,
                        isActive = true,
                    )
                firestoreRepository.createUser(userData)
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        suspend fun signOut() {
            firebaseAuth.signOut()
        }

        suspend fun getCurrentUserData(): User? = currentUserId?.let { firestoreRepository.getUser(it) }

        fun isAuthenticated(): Boolean = currentUser != null
    }
