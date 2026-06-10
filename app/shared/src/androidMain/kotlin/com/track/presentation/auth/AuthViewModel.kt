package com.track.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.track.data.repository.FirestoreRepository
import com.track.domain.models.User
import com.track.domain.models.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
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

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        init {
            // Listen for auth state changes to keep currentUser in sync
            viewModelScope.launch {
                auth.addAuthStateListener { firebaseAuth ->
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        // If we already have a user with the same ID, don't necessarily overwrite 
                        // until loadUserProfile completes, but we need some state here.
                        // Actually, if we're "logged in" in Firebase but profile isn't loaded yet,
                        // we might want a temporary placeholder or wait.
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
                    
                    // Force refresh profile immediately
                    val user = repository.getUser(uid) ?: createDefaultProfile(
                        uid = uid, 
                        email = email,
                        name = email.split("@")[0]
                    )

                    // Robust role check:
                    // 1. If no expectedRole, anyone can login (e.g. general refresh).
                    // 2. If expectedRole is CUSTOMER, only CUSTOMERs.
                    // 3. If expectedRole is STAFF, allow any internal role (STAFF, DRIVER, SUPER_ADMIN).
                    if (expectedRole != null) {
                        when (expectedRole) {
                            UserRole.CUSTOMER -> {
                                if (user.role != UserRole.CUSTOMER) {
                                    auth.signOut()
                                    throw Exception("Please use a customer account to log in here.")
                                }
                            }
                            UserRole.STAFF -> {
                                if (user.role == UserRole.CUSTOMER) {
                                    auth.signOut()
                                    throw Exception("Access denied. This portal is for staff only.")
                                }
                            }
                            else -> {
                                if (user.role != expectedRole) {
                                    auth.signOut()
                                    throw Exception("Insufficient permissions for this role.")
                                }
                            }
                        }
                    }

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
                    val newUser =
                        User(
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
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
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
                        createDefaultProfile(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            name = firebaseUser.displayName ?: firebaseUser.email?.split("@")?.get(0) ?: "User"
                        )
                    } else {
                        // Ensure email and name are updated if missing (e.g. Google login sync)
                        var updatedUser = user
                        var needsUpdate = false
                        
                        if (user.email.isBlank() && !firebaseUser.email.isNullOrBlank()) {
                            updatedUser = updatedUser.copy(email = firebaseUser.email!!)
                            needsUpdate = true
                        }
                        
                        if (user.name == "User" || user.name.isBlank()) {
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
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Failed to refresh profile", e)
                }
            }
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
                // Refresh the local state
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
