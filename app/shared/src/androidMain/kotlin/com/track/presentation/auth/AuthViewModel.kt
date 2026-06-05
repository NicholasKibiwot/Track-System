package com.track.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                    val user = repository.getUser(uid) ?: throw Exception("User profile not found.")

                    if (expectedRole != null && user.role != expectedRole && expectedRole != UserRole.STAFF) {
                        // Note: If expectedRole is STAFF, we might allow DRIVER and SUPER_ADMIN too if it's a generic staff login
                        // But if it's strictly CUSTOMER, we block others.
                        auth.signOut()
                        throw Exception("This account is not authorized for this login.")
                    }

                    if (expectedRole == UserRole.STAFF && user.role == UserRole.CUSTOMER) {
                        auth.signOut()
                        throw Exception("Customer accounts cannot use the staff login.")
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
            val uid = auth.currentUser?.uid
            viewModelScope.launch {
                if (uid != null) {
                    repository.updateUserOnlineStatus(uid, false)
                }
                auth.signOut()
                _currentUser.value = null
            }
        }

        fun isAuthenticated(): Boolean = auth.currentUser != null

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
            onSuccess: () -> Unit,
            onError: (String) -> Unit,
        ) {
            val uid = auth.currentUser?.uid ?: return onError("Not logged in.")
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    repository.updateUserProfile(uid, name, phone, shippingAddress)
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
