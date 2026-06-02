package com.track.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.track.data.repository.FirestoreRepository
import com.track.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
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
            // If already signed in, load user profile
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                loadUserProfile(firebaseUser.uid)
            }
        }

        fun login(
            email: String,
            password: String,
            onSuccess: (role: String) -> Unit,
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
                    val user =
                        repository.getUser(uid)
                            ?: throw Exception("User profile not found.")
                    _currentUser.value = user
                    _isLoading.value = false
                    onSuccess(user.role.name)
                } catch (e: Exception) {
                    _errorMessage.value = e.localizedMessage ?: "Login failed."
                    _isLoading.value = false
                }
            }
        }

        fun logout() {
            auth.signOut()
            _currentUser.value = null
        }

        fun isAuthenticated(): Boolean = auth.currentUser != null

        fun clearError() {
            _errorMessage.value = null
        }

        private fun loadUserProfile(uid: String) {
            viewModelScope.launch {
                try {
                    _currentUser.value = repository.getUser(uid)
                } catch (e: Exception) {
                    // silently fail on profile load
                }
            }
        }
    }
