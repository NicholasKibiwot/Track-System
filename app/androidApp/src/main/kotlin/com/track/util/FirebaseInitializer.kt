package com.track.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.track.data.repository.FirestoreRepository
import com.track.domain.models.User
import com.track.domain.models.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseInitializer @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Creates standard accounts for testing and initial setup.
     * Note: This should only be called once or handled with care.
     */
    fun initializeInternalUsers() {
        scope.launch {
            createInternalUser("admin@track.com", "Admin123!", "Global Admin", UserRole.SUPER_ADMIN)
            createInternalUser("staff@track.com", "Staff123!", "Warehouse Staff", UserRole.STAFF)
            createInternalUser("driver@track.com", "Driver123!", "Delivery Driver", UserRole.DRIVER)
        }
    }

    private suspend fun createInternalUser(
        email: String,
        password: String,
        name: String,
        role: UserRole
    ) {
        try {
            Log.d("FirebaseInit", "Checking for $email...")
            
            // Note: Firebase doesn't have a direct "getUserByEmail" in the client SDK without Admin SDK.
            // We attempt to sign in to see if it exists, or better, just attempt to create.
            // If it exists, creation fails.
            
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: return
                
                val user = User(
                    id = uid,
                    email = email,
                    name = name,
                    role = role,
                    isActive = true
                )
                
                repository.createUser(user)
                Log.d("FirebaseInit", "Successfully created $role: $email")
            } catch (e: Exception) {
                if (e.message?.contains("email address is already in use") == true) {
                    Log.d("FirebaseInit", "User $email already exists.")
                    // Optional: Update role in Firestore even if auth exists
                    // repository.updateUserRole(email, role)
                } else {
                    Log.e("FirebaseInit", "Error creating $email", e)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Critical error in setup for $email", e)
        }
    }
}
