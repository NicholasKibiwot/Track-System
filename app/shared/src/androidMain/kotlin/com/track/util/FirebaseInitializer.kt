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
     */
    fun initializeInternalUsers() {
        Log.d("FirebaseInit", "Starting internal users initialization...")
        scope.launch {
            createInternalUser("admin@track.com", "Admin123!", "Global Admin", UserRole.SUPER_ADMIN)
            createInternalUser("staff@track.com", "Staff123!", "Warehouse Staff", UserRole.STAFF)
            createInternalUser("driver@track.com", "Driver123!", "Delivery Driver", UserRole.DRIVER)
            Log.d("FirebaseInit", "Finished internal users initialization task.")
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
            
            // Try to create the user
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
                Log.i("FirebaseInit", "Successfully created $role: $email")
            } catch (e: Exception) {
                // If user already exists in Auth, we should still ensure their role is correct in Firestore
                if (e.message?.contains("already in use", ignoreCase = true) == true) {
                    Log.d("FirebaseInit", "User $email already exists in Auth. Verifying Firestore profile...")
                    
                    // We can't get UID by email easily in Client SDK, but if we just login we can.
                    // However, we don't want to disrupt the app state.
                    // Instead, we trust that if they login, the AuthViewModel will handle profile creation.
                    // BUT, to be sure, we can try a temporary login to fix the role if we have the password.
                    try {
                        val signin = auth.signInWithEmailAndPassword(email, password).await()
                        val uid = signin.user?.uid
                        if (uid != null) {
                            val profile = repository.getUser(uid)
                            if (profile == null || profile.role != role) {
                                repository.createUser(User(id = uid, email = email, name = name, role = role, isActive = true))
                                Log.i("FirebaseInit", "Updated/Fixed profile role for $email to $role")
                            }
                        }
                        auth.signOut() // Sign out immediately after fixing
                    } catch (signInEx: Exception) {
                        Log.w("FirebaseInit", "Could not verify/fix profile for $email: ${signInEx.message}")
                    }
                } else {
                    Log.e("FirebaseInit", "Error creating $email", e)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Critical error in setup for $email", e)
        }
    }
}
