package com.track.presentation.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.track.data.repository.FirestoreRepository
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.domain.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.track.util.CommonHiltViewModel
import com.track.util.CommonInject

@CommonHiltViewModel
open class StaffViewModel
@CommonInject
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

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        loadAllOrders()
        loadAllProducts()
    }

    private fun loadAllOrders() {
        viewModelScope.launch {
            repository.getOrdersFlow().collect { _orders.value = it }
        }
    }

    private fun loadAllProducts() {
        viewModelScope.launch {
            repository.getProductsFlow().collect { _products.value = it }
        }
    }

    fun lookupOrder(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _lookupResult.value = null
            try {
                // Try searching in local state first for trackingNumber or ID
                val fromLocal = _orders.value.find {
                    it.trackingNumber.equals(query, ignoreCase = true) ||
                            it.id.equals(query, ignoreCase = true)
                }
                
                if (fromLocal != null) {
                    _lookupResult.value = fromLocal
                } else {
                    // Try direct ID lookup via repository
                    val order = repository.getOrderById(query)
                    if (order != null) {
                        _lookupResult.value = order
                    } else {
                        _errorMessage.value = "No order found for \"$query\"."
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
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
                // Local state update will happen via Flow collection automatically
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update status: ${e.message}"
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

