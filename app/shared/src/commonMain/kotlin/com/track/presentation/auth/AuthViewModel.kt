package com.track.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.track.data.repository.FirestoreRepository
import com.track.domain.models.User
import com.track.domain.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class AuthViewModel(
    private val repository: FirestoreRepository,
) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(
        email: String,
        password: String,
        expectedRole: UserRole? = null,
        onSuccess: (User) -> Unit,
    ) {
        // Implementation will be handled by subclasses or expected/actual if needed
        // For now, let's keep it simple and assume the repository handles the auth check if possible
        // but Firebase Auth is platform specific.
    }

    protected fun validateUserRole(user: User, expectedRole: UserRole?) {
        if (expectedRole == null) return

        val isUnauthorized = when (expectedRole) {
            UserRole.CUSTOMER -> user.role != UserRole.CUSTOMER
            UserRole.STAFF -> user.role == UserRole.CUSTOMER
            else -> user.role != expectedRole
        }

        if (isUnauthorized) {
            val message = when (expectedRole) {
                UserRole.CUSTOMER -> "Please use a customer account to log in here."
                UserRole.STAFF -> "Access denied. This portal is for staff only."
                else -> "Insufficient permissions for this role."
            }
            throw Exception(message)
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _currentUser.value?.id?.let { uid ->
                    repository.updateUserOnlineStatus(uid, false)
                }
            } catch (e: Exception) {
            } finally {
                _currentUser.value = null
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
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
        val uid = _currentUser.value?.id ?: return onError("Not logged in.")
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
                _errorMessage.value = e.message ?: "Update failed."
                _isLoading.value = false
                onError(_errorMessage.value ?: "Update failed.")
            }
        }
    }
}
