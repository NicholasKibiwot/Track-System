package com.track.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.track.data.repository.FirestoreRepository
import com.track.domain.models.Order
import com.track.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel // 👈 CRITICAL: Tells Hilt to manage this ViewModel
class AdminViewModel
    @Inject
    constructor( // 👈 CRITICAL: Enables dependency injection
        private val repository: FirestoreRepository,
    ) : ViewModel() {
        // Real-time flow of orders from Firestore
        val orders: StateFlow<List<Order>> =
            repository
                .getOrdersFlow()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Loading state (optional - can be removed if not needed)
        private val _isLoading = kotlinx.coroutines.flow.MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading
    }
