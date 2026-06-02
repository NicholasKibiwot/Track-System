// com.track.presentation.auth/AuthViewModel.kt
package com.track.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.track.data.repository.AuthRepository
import com.track.domain.models.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
        val authState: StateFlow<AuthState> = _authState.asStateFlow()

        // ✅ SINGLE LOGIN METHOD FOR ALL ROLES
        fun login(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                _authState.value = AuthState.Loading
                val result = authRepository.signIn(email, password)

                _authState.value =
                    when {
                        result.isSuccess -> AuthState.Success(result.getOrNull()!!)
                        else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
                    }
            }
        }

        fun logout() {
            viewModelScope.launch {
                authRepository.signOut()
                _authState.value = AuthState.Idle
            }
        }
    }

sealed class AuthState {
    object Idle : AuthState()

    object Loading : AuthState()

    data class Success(
        val role: UserRole,
    ) : AuthState()

    data class Error(
        val message: String,
    ) : AuthState()
}
