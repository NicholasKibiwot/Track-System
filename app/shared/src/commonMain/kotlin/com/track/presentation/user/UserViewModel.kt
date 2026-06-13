package com.track.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.data.repository.FirestoreRepository
import com.track.util.CommonHiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.track.util.CommonInject

@CommonHiltViewModel
class UserViewModel
    @CommonInject
    constructor(
        private val repository: FirestoreRepository,
    ) : ViewModel() {
        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _statusMessage = MutableStateFlow("")
        val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

        // Hardcoded driver ID for now. In production, this comes from Firebase Auth.
        private val driverId = "DRV-04"

        val currentJob =
            repository
                .getOrdersFlow()
                .map { list -> list.find { it.driverId == driverId && it.orderStatus != OrderStatus.DELIVERED } }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        fun startTransit(orderId: String) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    repository.updateOrderStatus(orderId, OrderStatus.INTRANSIT)
                    _statusMessage.value = "Journey started!"
                } catch (e: Exception) {
                    _statusMessage.value = "Error starting journey"
                } finally {
                    _isLoading.value = false
                }
            }
        }

        fun markDelivered(orderId: String) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    repository.updateOrderStatus(orderId, OrderStatus.DELIVERED)
                    _statusMessage.value = "Package delivered!"
                } catch (e: Exception) {
                    _statusMessage.value = "Error updating status"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

