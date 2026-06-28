package com.track.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.track.data.repository.FirestoreRepository
import com.track.models.User
import com.track.models.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseInitializer @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository,
) {
    private val scope = CoroutineScope(Dispatchers.IO)

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
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: return
                saveUserToFirestore(uid, email, name, role)
            } catch (e: Exception) {
                if (e.message?.contains("already in use", ignoreCase = true) == true) {
                    verifyExistingUser(email, password, name, role)
                } else {
                    Log.e("FirebaseInit", "Error creating $email", e)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Critical error in setup for $email", e)
        }
    }

    private suspend fun saveUserToFirestore(uid: String, email: String, name: String, role: UserRole) {
        val user = User(
            id = uid,
            email = email,
            name = name,
            role = role,
            isActive = true
        )
        repository.createUser(user)
        Log.i("FirebaseInit", "Successfully created $role: $email")
    }

    private suspend fun verifyExistingUser(email: String, password: String, name: String, role: UserRole) {
        Log.d("FirebaseInit", "User $email already exists. Verifying Firestore profile...")
        try {
            val signin = auth.signInWithEmailAndPassword(email, password).await()
            val uid = signin.user?.uid
            if (uid != null) {
                val profile = repository.getUser(uid)
                if ((profile == null) || (profile.role != role)) {
                    val user = User(id = uid, email = email, name = name, role = role, isActive = true)
                    repository.createUser(user)
                    Log.i("FirebaseInit", "Updated/Fixed profile role for $email to $role")
                }
            }
            auth.signOut()
        } catch (signInEx: Exception) {
            Log.w("FirebaseInit", "Could not verify/fix profile for $email: ${signInEx.message}")
        }
    }
}
