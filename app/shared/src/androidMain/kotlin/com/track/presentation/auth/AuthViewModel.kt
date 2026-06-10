package com.track.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.track.data.repository.FirestoreRepository
import com.track.domain.models.User
import com.track.domain.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

open class AuthViewModel
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val repository: FirestoreRepository,
    ) : ViewModel() {
        private val _currentUser = MutableStateFlow<User?>(null)
        val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

        private val _isLoading = MutableStateFlow(value = false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        init {
            // Listen for auth state changes to keep currentUser in sync
            viewModelScope.launch {
                auth.addAuthStateListener { firebaseAuth ->
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        loadUserProfile(firebaseUser.uid)
                    } else {
                        _currentUser.value = null
                    }
                }
            }
        }

        fun login(
            email: String,
            password: String,
            expectedRole: UserRole? = null,
            onSuccess: (User) -> Unit,
        ) {
            if (email.isBlank() || password.isBlank()) {
                _errorMessage.value = "Please enter your email and password."
                return
            }
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    val result = auth.signInWithEmailAndPassword(email, password).await()
                    val uid = result.user?.uid ?: throw Exception("Login failed.")
                    
                    val user = repository.getUser(uid) ?: createDefaultProfile(
                        uid = uid, 
                        email = email,
                        name = email.split("@")[0]
                    )

                    validateUserRole(user, expectedRole)

                    repository.updateUserOnlineStatus(uid, true)
                    _currentUser.value = user
                    _isLoading.value = false
                    onSuccess(user)
                } catch (e: Exception) {
                    _errorMessage.value = e.localizedMessage ?: "Login failed."
                    _isLoading.value = false
                }
            }
        }

        private fun validateUserRole(user: User, expectedRole: UserRole?) {
            if (expectedRole == null) return

            val isUnauthorized = when (expectedRole) {
                UserRole.CUSTOMER -> user.role != UserRole.CUSTOMER
                UserRole.STAFF -> user.role == UserRole.CUSTOMER
                else -> user.role != expectedRole
            }

            if (isUnauthorized) {
                auth.signOut()
                val message = when (expectedRole) {
                    UserRole.CUSTOMER -> "Please use a customer account to log in here."
                    UserRole.STAFF -> "Access denied. This portal is for staff only."
                    else -> "Insufficient permissions for this role."
                }
                throw Exception(message)
            }
        }

        private suspend fun createDefaultProfile(uid: String, email: String, name: String): User {
            val newUser = User(
                id = uid, 
                email = email, 
                name = name, 
                role = UserRole.CUSTOMER,
                isActive = true,
                isOnline = true
            )
            repository.createUser(newUser)
            _currentUser.value = newUser
            return newUser
        }

        fun register(
            email: String,
            password: String,
            name: String,
            phone: String,
            onSuccess: (User) -> Unit,
        ) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    val result = auth.createUserWithEmailAndPassword(email, password).await()
                    val uid = result.user?.uid ?: throw Exception("Registration failed.")
                    val newUser = User(
                        id = uid,
                        email = email,
                        name = name,
                        phone = phone,
                        role = UserRole.CUSTOMER,
                        isOnline = true,
                    )
                    repository.createUser(newUser)
                    _currentUser.value = newUser
                    _isLoading.value = false
                    onSuccess(newUser)
                } catch (e: Exception) {
                    _errorMessage.value = e.localizedMessage ?: "Registration failed."
                    _isLoading.value = false
                }
            }
        }

        fun logout() {
            viewModelScope.launch {
                try {
                    auth.currentUser?.uid?.let { uid ->
                        repository.updateUserOnlineStatus(uid, false)
                    }
                } catch (e: Exception) {
                    // Ignore status update errors on logout
                } finally {
                    auth.signOut()
                    _currentUser.value = null
                }
            }
        }

        fun isAuthenticated(): Boolean = auth.currentUser != null

        fun refreshProfile(idToken: String? = null) {
            val firebaseUser = auth.currentUser ?: return
            viewModelScope.launch {
                try {
                    val user = repository.getUser(firebaseUser.uid)
                    if (user == null) {
                        handleMissingProfile(firebaseUser)
                    } else {
                        updateProfileIfNeeded(user, firebaseUser)
                    }
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Failed to refresh profile", e)
                }
            }
        }

        private suspend fun handleMissingProfile(firebaseUser: com.google.firebase.auth.FirebaseUser) {
            createDefaultProfile(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName ?: firebaseUser.email?.split("@")?.get(0) ?: "User"
            )
        }

        private suspend fun updateProfileIfNeeded(user: User, firebaseUser: com.google.firebase.auth.FirebaseUser) {
            var updatedUser = user
            var needsUpdate = false
            
            if (user.email.isBlank() && !firebaseUser.email.isNullOrBlank()) {
                updatedUser = updatedUser.copy(email = firebaseUser.email!!)
                needsUpdate = true
            }
            
            if ((user.name == "User") || user.name.isBlank()) {
                 val newName = firebaseUser.displayName ?: firebaseUser.email?.split("@")?.get(0)
                 if (!newName.isNullOrBlank()) {
                     updatedUser = updatedUser.copy(name = newName)
                     needsUpdate = true
                 }
            }

            if (needsUpdate) {
                repository.createUser(updatedUser)
                _currentUser.value = updatedUser
            } else {
                _currentUser.value = user
            }
            repository.updateUserOnlineStatus(firebaseUser.uid, true)
        }

        fun clearError() {
            _errorMessage.value = null
        }

        private fun loadUserProfile(uid: String) {
            viewModelScope.launch {
                try {
                    val user = repository.getUser(uid)
                    if (user != null) {
                        repository.updateUserOnlineStatus(uid, true)
                        _currentUser.value = user
                    }
                } catch (e: Exception) {
                    // silently fail on profile load
                }
            }
        }

        fun updateProfile(
            name: String,
            phone: String,
            shippingAddress: String,
            dob: String = "",
            country: String = "",
            onSuccess: () -> Unit,
            onError: (String) -> Unit,
        ) {
            val uid = auth.currentUser?.uid ?: return onError("Not logged in.")
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    repository.updateUserProfile(
                        userId = uid,
                        name = name,
                        phone = phone,
                        shippingAddress = shippingAddress,
                        dob = dob,
                        country = country
                    )
                    val updated = repository.getUser(uid)
                    if (updated != null) _currentUser.value = updated
                    _isLoading.value = false
                    onSuccess()
                } catch (e: Exception) {
                    _errorMessage.value = e.localizedMessage ?: "Update failed."
                    _isLoading.value = false
                    onError(_errorMessage.value ?: "Update failed.")
                }
            }
        }
    }
