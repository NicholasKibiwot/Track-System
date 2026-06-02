package com.track.presentation.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.track.data.repository.FirestoreRepository
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffViewModel
    @Inject
    constructor(
        private val repository: FirestoreRepository,
    ) : ViewModel() {
        private val _lookupResult = MutableStateFlow<Order?>(null)
        val lookupResult: StateFlow<Order?> = _lookupResult.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        private val _orders = MutableStateFlow<List<Order>>(emptyList())
        val orders: StateFlow<List<Order>> = _orders.asStateFlow()

        init {
            loadAllOrders()
        }

        private fun loadAllOrders() {
            viewModelScope.launch {
                repository.getOrdersFlow().collect { _orders.value = it }
            }
        }

        fun lookupOrder(query: String) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                _lookupResult.value = null
                try {
                    // Try direct ID lookup first
                    val order = repository.getOrderById(query)
                    if (order != null) {
                        _lookupResult.value = order
                    } else {
                        // Fall back to searching by trackingNumber in local state
                        val fromLocal =
                            _orders.value.find {
                                it.trackingNumber.equals(query, ignoreCase = true) ||
                                    it.id.equals(query, ignoreCase = true)
                            }
                        if (fromLocal != null) {
                            _lookupResult.value = fromLocal
                        } else {
                            _errorMessage.value = "No order found for \"$query\"."
                        }
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error: ${e.localizedMessage}"
                } finally {
                    _isLoading.value = false
                }
            }
        }

        fun updateOrderStatus(
            orderId: String,
            newStatus: OrderStatus,
        ) {
            viewModelScope.launch {
                try {
                    repository.updateOrderStatus(orderId, newStatus)
                    // Refresh lookup result if it's the same order
                    if (_lookupResult.value?.id == orderId) {
                        _lookupResult.value = _lookupResult.value?.copy(orderStatus = newStatus)
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to update status: ${e.localizedMessage}"
                }
            }
        }

        fun clearError() {
            _errorMessage.value = null
        }

        fun clearResult() {
            _lookupResult.value = null
        }
    }
