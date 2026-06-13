package com.track.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.track.domain.models.User
import com.track.domain.models.UserRole
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreRepository: FirestoreRepository,
) : AuthRepository {
    
    override suspend fun signIn(email: String, password: String): Result<UserRole> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("User is null"))

            val userData = firestoreRepository.getUser(user.uid)
            val role = userData?.role ?: UserRole.CUSTOMER

            Result.success(role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, name: String, phone: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("User is null"))

            val user = User(
                id = firebaseUser.uid,
                email = email,
                name = name,
                displayName = name,
                phone = phone,
                phoneNumber = phone,
                role = UserRole.CUSTOMER,
                isActive = true
            )
            firestoreRepository.createUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override suspend fun getCurrentUserData(): User? = getCurrentUserId()?.let { firestoreRepository.getUser(it) }

    override fun isAuthenticated(): Boolean = firebaseAuth.currentUser != null
}
