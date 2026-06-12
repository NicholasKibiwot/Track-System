package com.track.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.track.data.repository.FirestoreRepository
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.domain.models.Product
import com.track.domain.models.StaffProfile
import com.track.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class SuperAdminViewModel
    @Inject
    constructor(
        private val repository: FirestoreRepository,
    ) : ViewModel() {
        // ── Orders ──────────────────────────────────────────────────────────────

        private val _orders = MutableStateFlow<List<Order>>(emptyList())
        val orders: StateFlow<List<Order>> = _orders.asStateFlow()

        // ── Staff users (role == STAFF) ──────────────────────────────────────────

        private val _staffUsers = MutableStateFlow<List<User>>(emptyList())
        val staffUsers: StateFlow<List<User>> = _staffUsers.asStateFlow()

        // ── Staff profiles (staff collection) ────────────────────────────────────

        private val _staffProfiles = MutableStateFlow<List<StaffProfile>>(emptyList())
        val staffProfiles: StateFlow<List<StaffProfile>> = _staffProfiles.asStateFlow()

        // ── Products ─────────────────────────────────────────────────────────────

        private val _products = MutableStateFlow<List<Product>>(emptyList())
        val products: StateFlow<List<Product>> = _products.asStateFlow()

        // ── All users ─────────────────────────────────────────────────────────────

        private val _allUsers = MutableStateFlow<List<User>>(emptyList())
        val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

        // ── Loading & error ───────────────────────────────────────────────────────

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        // ── Dashboard stats ───────────────────────────────────────────────────────

        val totalOrders: Int get() = _orders.value.size
        val pendingOrders: Int get() =
            _orders.value.count {
                it.orderStatus == OrderStatus.PENDING || it.orderStatus == OrderStatus.PROCESSING
            }
        val inTransitOrders: Int get() =
            _orders.value.count {
                it.orderStatus == OrderStatus.INTRANSIT
            }
        val deliveredOrders: Int get() =
            _orders.value.count {
                it.orderStatus == OrderStatus.DELIVERED
            }
        val activeStaff: Int get() = _staffUsers.value.count { it.isActive }

        init {
            loadOrders()
            loadStaffUsers()
            loadStaffProfiles()
            loadProducts()
            loadAllUsers()
        }

        // ── Data loaders ──────────────────────────────────────────────────────────

        private fun loadOrders() {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    repository.getOrdersFlow().collect { orders ->
                        _orders.value = orders
                        _isLoading.value = false
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load orders: ${e.localizedMessage}"
                    _isLoading.value = false
                }
            }
        }

        private fun loadStaffUsers() {
            viewModelScope.launch {
                try {
                    repository.getStaffUsersFlow().collect { users ->
                        _staffUsers.value = users
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load staff: ${e.localizedMessage}"
                }
            }
        }

        private fun loadStaffProfiles() {
            viewModelScope.launch {
                try {
                    repository.getStaffProfilesFlow().collect { profiles ->
                        _staffProfiles.value = profiles
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load staff profiles: ${e.localizedMessage}"
                }
            }
        }

        private fun loadProducts() {
            viewModelScope.launch {
                try {
                    repository.getProductsFlow().collect { products ->
                        _products.value = products
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load products: ${e.localizedMessage}"
                }
            }
        }

        private fun loadAllUsers() {
            viewModelScope.launch {
                try {
                    repository.getUsersFlow().collect { users ->
                        _allUsers.value = users
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load users: ${e.localizedMessage}"
                }
            }
        }

        // ── Order actions ─────────────────────────────────────────────────────────

        fun updateOrderStatus(
            orderId: String,
            newStatus: OrderStatus,
        ) {
            viewModelScope.launch {
                try {
                    repository.updateOrderStatus(orderId, newStatus)
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to update order: ${e.localizedMessage}"
                }
            }
        }

        fun createOrder(
            order: Order,
            onSuccess: (String) -> Unit = {},
        ) {
            viewModelScope.launch {
                try {
                    val id = repository.createOrder(order)
                    onSuccess(id)
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to create order: ${e.localizedMessage}"
                }
            }
        }

        // ── Staff actions ─────────────────────────────────────────────────────────

        fun toggleStaffActive(
            userId: String,
            isActive: Boolean,
        ) {
            viewModelScope.launch {
                try {
                    repository.updateUserActiveStatus(userId, isActive)
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to update staff: ${e.localizedMessage}"
                }
            }
        }

        fun toggleStaffProfileActive(
            staffDocId: String,
            isActive: Boolean,
        ) {
            viewModelScope.launch {
                try {
                    repository.updateStaffActiveStatus(staffDocId, isActive)
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to update staff profile: ${e.localizedMessage}"
                }
            }
        }

        fun createUser(
            user: User,
            onSuccess: (String) -> Unit = {},
        ) {
            viewModelScope.launch {
                try {
                    val id = repository.createUser(user)
                    onSuccess(id)
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to create user: ${e.localizedMessage}"
                }
            }
        }

        // ── Product actions ───────────────────────────────────────────────────────

        fun createProduct(
            product: Product,
            onSuccess: (String) -> Unit = {},
        ) {
            viewModelScope.launch {
                try {
                    val id = repository.createProduct(product)
                    onSuccess(id)
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to create product: ${e.localizedMessage}"
                }
            }
        }

        // ── Utility ───────────────────────────────────────────────────────────────

        fun clearError() {
            _errorMessage.value = null
        }

        fun getOrderById(orderId: String): Order? = _orders.value.find { it.id == orderId }

        fun getUserById(userId: String): User? = _allUsers.value.find { it.id == userId }
    }
