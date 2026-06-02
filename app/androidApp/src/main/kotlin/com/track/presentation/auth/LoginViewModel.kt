package com.track.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor() : ViewModel() {
        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        // Mock login for Admin
        fun loginAsAdmin(
            email: String,
            password: String,
            onSuccess: () -> Unit,
        ) {
            _isLoading.value = true
            _errorMessage.value = null
            viewModelScope.launch {
                delay(800) // Simulate network call
                if (email.isBlank() || password.isBlank()) {
                    _errorMessage.value = "Please enter email and password"
                    _isLoading.value = false
                } else {
                    _isLoading.value = false
                    onSuccess() // Navigate to Admin Dashboard
                }
            }
        }

        // Mock login for Staff/Driver
        fun loginAsStaff(
            email: String,
            password: String,
            onSuccess: () -> Unit,
        ) {
            _isLoading.value = true
            _errorMessage.value = null
            viewModelScope.launch {
                delay(800) // Simulate network call
                if (email.isBlank() || password.isBlank()) {
                    _errorMessage.value = "Please enter email and password"
                    _isLoading.value = false
                } else {
                    _isLoading.value = false
                    onSuccess() // Navigate to User Dashboard
                }
            }
        }

        fun clearError() {
            _errorMessage.value = null
        }
    }
