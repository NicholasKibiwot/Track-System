package com.track.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.track.data.repository.FirestoreRepository
import com.track.domain.models.Order
import com.track.domain.models.OrderItem
import com.track.domain.models.OrderStatus
import com.track.domain.models.Product
import com.track.domain.models.TrackingRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class CustomerViewModel
    @Inject
    constructor(
        private val repository: FirestoreRepository,
    ) : ViewModel() {
        // ── Products ──────────────────────────────────────────────────────────────

        private val _products = MutableStateFlow<List<Product>>(emptyList())
        val products: StateFlow<List<Product>> = _products.asStateFlow()

        // ── Customer orders ───────────────────────────────────────────────────────

        private val _myOrders = MutableStateFlow<List<Order>>(emptyList())
        val myOrders: StateFlow<List<Order>> = _myOrders.asStateFlow()

        // ── Tracking record for a watched order ──────────────────────────────────

        private val _trackingRecord = MutableStateFlow<TrackingRecord?>(null)
        val trackingRecord: StateFlow<TrackingRecord?> = _trackingRecord.asStateFlow()

        // ── Selected order for detail / tracking view ─────────────────────────────

        private val _selectedOrder = MutableStateFlow<Order?>(null)
        val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

        // ── Cart (in-memory only, not persisted to Firestore) ─────────────────────

        private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
        val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

        val cartTotal: Double
            get() = _cartItems.value.sumOf { it.product.price * it.quantity }

        val cartCount: Int
            get() = _cartItems.value.sumOf { it.quantity }

        // ── Loading & error state ─────────────────────────────────────────────────

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        private val _orderSuccess = MutableStateFlow<String?>(null)
        val orderSuccess: StateFlow<String?> = _orderSuccess.asStateFlow()

        // ── Current logged-in customer ────────────────────────────────────────────

        private var currentCustomerId: String = ""
        private var currentCustomerName: String = ""

        // ─────────────────────────────────────────────────────────────────────────

        init {
            loadProducts()
        }

        // ── Auth wiring ───────────────────────────────────────────────────────────

        /**
         * Call this right after login so the ViewModel knows which
         * customer is logged in and loads their order history.
         */
        fun setCustomer(
            customerId: String,
            customerName: String,
        ) {
            currentCustomerId = customerId
            currentCustomerName = customerName
            loadMyOrders()
        }

        // ── Data loaders ──────────────────────────────────────────────────────────

        private fun loadProducts() {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    repository.getProductsFlow().collect { list ->
                        _products.value = list
                        _isLoading.value = false
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load products: ${e.localizedMessage}"
                    _isLoading.value = false
                }
            }
        }

        private fun loadMyOrders() {
            if (currentCustomerId.isBlank()) return
            viewModelScope.launch {
                try {
                    repository.getOrdersByCustomerFlow(currentCustomerId).collect { orders ->
                        _myOrders.value = orders.sortedByDescending { it.createdAt.seconds }
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load your orders: ${e.localizedMessage}"
                }
            }
        }

        fun watchTrackingRecord(orderId: String) {
            viewModelScope.launch {
                try {
                    repository.getTrackingRecordFlow(orderId).collect { record ->
                        _trackingRecord.value = record
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load tracking info: ${e.localizedMessage}"
                }
            }
        }

        fun selectOrder(order: Order) {
            _selectedOrder.value = order
            watchTrackingRecord(order.id)
        }

        fun clearSelectedOrder() {
            _selectedOrder.value = null
            _trackingRecord.value = null
        }

        // ── Cart actions ──────────────────────────────────────────────────────────

        fun addToCart(product: Product) {
            val current = _cartItems.value.toMutableList()
            val existingIndex = current.indexOfFirst { it.product.id == product.id }
            if (existingIndex >= 0) {
                val existing = current[existingIndex]
                if (existing.quantity < product.stock) {
                    current[existingIndex] = existing.copy(quantity = existing.quantity + 1)
                } else {
                    _errorMessage.value = "Only ${product.stock} unit(s) available in stock."
                    return
                }
            } else {
                if (product.stock <= 0) {
                    _errorMessage.value = "${product.name} is out of stock."
                    return
                }
                current.add(CartItem(product = product, quantity = 1))
            }
            _cartItems.value = current
        }

        fun removeFromCart(productId: String) {
            _cartItems.value = _cartItems.value.filter { it.product.id != productId }
        }

        fun increaseQuantity(productId: String) {
            val current = _cartItems.value.toMutableList()
            val index = current.indexOfFirst { it.product.id == productId }
            if (index >= 0) {
                val item = current[index]
                if (item.quantity < item.product.stock) {
                    current[index] = item.copy(quantity = item.quantity + 1)
                    _cartItems.value = current
                } else {
                    _errorMessage.value =
                        "Maximum available stock reached for ${item.product.name}."
                }
            }
        }

        fun decreaseQuantity(productId: String) {
            val current = _cartItems.value.toMutableList()
            val index = current.indexOfFirst { it.product.id == productId }
            if (index >= 0) {
                val item = current[index]
                if (item.quantity > 1) {
                    current[index] = item.copy(quantity = item.quantity - 1)
                } else {
                    current.removeAt(index)
                }
                _cartItems.value = current
            }
        }

        fun clearCart() {
            _cartItems.value = emptyList()
        }

        // ── Place order ───────────────────────────────────────────────────────────

        private fun generateShortTrackingCode(): String {
            val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
            return (1..8).map { chars.random() }.joinToString("")
        }

        /**
         * Builds an Order from the current cart and writes it to
         * Firestore's "orders" collection.
         */
        fun placeOrder(
            paymentMethod: String,
            origin: String,
            destination: String,
            deliveryType: String,
            onSuccess: (orderId: String) -> Unit = {},
        ) {
            if (_cartItems.value.isEmpty()) {
                _errorMessage.value = "Your cart is empty. Add products before checking out."
                return
            }
            if (currentCustomerId.isBlank()) {
                _errorMessage.value = "You must be logged in to place an order."
                return
            }

            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val orderItems =
                        _cartItems.value.map { cartItem ->
                            OrderItem(
                                id = UUID.randomUUID().toString(),
                                productId = cartItem.product.id,
                                productName = cartItem.product.name,
                                quantity = cartItem.quantity,
                                unitPrice = cartItem.product.price,
                                imageUrl = cartItem.product.imageUrl.ifBlank { null },
                            )
                        }

                    val total = orderItems.sumOf { it.unitPrice * it.quantity }
                    val trackingNumber = generateShortTrackingCode()

                    val newOrder =
                        Order(
                            id = "",
                            trackingNumber = trackingNumber,
                            customerId = currentCustomerId,
                            customerName = currentCustomerName,
                            items = orderItems,
                            totalAmount = total,
                            paymentMethod = paymentMethod,
                            paymentStatus = "PENDING",
                            orderStatus = OrderStatus.PENDING,
                            deliveryType = deliveryType,
                            driverId = null,
                            driverName = null,
                            origin = origin,
                            destination = destination,
                            currentLocation = null,
                            locationHistory = emptyList(),
                            createdAt = Timestamp.now(),
                            updatedAt = Timestamp.now(),
                        )

                    val orderId = repository.createOrder(newOrder)
                    clearCart()
                    _orderSuccess.value = trackingNumber
                    _isLoading.value = false
                    onSuccess(orderId)
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to place order: ${e.localizedMessage}"
                    _isLoading.value = false
                }
            }
        }

        // ── Utility ───────────────────────────────────────────────────────────────

        fun getProductById(productId: String): Product? = _products.value.find { it.id == productId }

        fun getOrderById(orderId: String): Order? = _myOrders.value.find { it.id == orderId }

        fun clearError() {
            _errorMessage.value = null
        }

        fun clearOrderSuccess() {
            _orderSuccess.value = null
        }
    }

// ── Cart item model ───────────────────────────────────────────────────────────

/**
 * Represents a single product line in the customer's local in-memory cart.
 */
data class CartItem(
    val product: Product,
    val quantity: Int,
)
