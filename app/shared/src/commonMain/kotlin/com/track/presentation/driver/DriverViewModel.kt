package com.track.presentation.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.track.data.repository.FirestoreRepository
import com.track.data.TrackingStateHolder
import com.track.domain.models.GeoLocation
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.domain.models.TrackingLocation
import com.track.util.CommonHiltViewModel
import com.track.util.TrackTimestamp
import com.track.util.getCurrentTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.track.util.CommonInject
import kotlin.time.Clock

@CommonHiltViewModel
open class DriverViewModel
    @CommonInject
    constructor(
        private val repository: FirestoreRepository,
        private val trackingStateHolder: TrackingStateHolder,
    ) : ViewModel() {
        private val _assignedOrders = MutableStateFlow<List<Order>>(emptyList())
        val assignedOrders: StateFlow<List<Order>> = _assignedOrders.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        private val _currentDriverId = MutableStateFlow<String>("")

        init {
            observeOrders()
        }

        /**
         * Call this after authentication to set the logged-in driver's userId
         * so orders are filtered correctly.
         */
        fun setDriverId(driverId: String) {
            _currentDriverId.value = driverId
            observeOrders()
        }

        private fun observeOrders() {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val driverId = _currentDriverId.value
                    val flow =
                        if (driverId.isNotBlank()) {
                            repository.getOrdersByDriverFlow(driverId)
                        } else {
                            repository.getOrdersFlow()
                        }
                    flow.collect { allOrders ->
                        _assignedOrders.value =
                            allOrders.filter { order ->
                                order.orderStatus != OrderStatus.DELIVERED &&
                                    order.orderStatus != OrderStatus.CANCELLED
                            }
                        _isLoading.value = false
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load orders: ${e.message}"
                    _isLoading.value = false
                }
            }
        }

        /**
         * Update the status of a specific order in Firestore.
         */
        fun updateOrderStatus(
            orderId: String,
            newStatus: OrderStatus,
        ) {
            viewModelScope.launch {
                try {
                    repository.updateOrderStatus(orderId, newStatus)
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to update status: ${e.message}"
                }
            }
        }

        /**
         * Simulate a GPS location update for an order.
         * In production, replace with real device GPS coordinates.
         */
        fun simulateLocationUpdate(orderId: String) {
            viewModelScope.launch {
                try {
                    val order = _assignedOrders.value.find { it.id == orderId } ?: return@launch
                    val current = order.currentLocation

                    // Nudge current coordinates slightly to simulate movement
                    val baseLat = current?.latitude ?: -1.2921
                    val baseLng = current?.longitude ?: 36.8219

                    val newLat = baseLat + kotlin.random.Random.nextDouble(-0.008, 0.008)
                    val newLng = baseLng + kotlin.random.Random.nextDouble(-0.008, 0.008)

                    // Update orders collection
                    val newGeoLocation =
                        GeoLocation(
                            id = "LOC-${getCurrentTimeMillis()}",
                            latitude = newLat,
                            longitude = newLng,
                            accuracyMeters = kotlin.random.Random.nextDouble(5.0, 20.0),
                            timestamp = TrackTimestamp.now(),
                            address = "En route to ${order.destination}",
                        )
                    repository.updateOrderLocation(orderId, newGeoLocation)

                    // Update tracking collection (uses flat lat/lng fields)
                    val newTrackingLocation =
                        TrackingLocation(
                            address = newGeoLocation.address,
                            lat = newLat,
                            lng = newLng,
                        )
                    repository.updateTrackingLocation(
                        orderId = orderId,
                        newLocation = newTrackingLocation,
                        driverId = _currentDriverId.value,
                    )
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to update location: ${e.message}"
                }
            }
        }

        /**
         * Update with real GPS coordinates from the device.
         */
        fun updateRealLocation(
            orderId: String,
            lat: Double,
            lng: Double,
            address: String,
        ) {
            viewModelScope.launch {
                try {
                    val newGeoLocation =
                        GeoLocation(
                            id = "LOC-${getCurrentTimeMillis()}",
                            latitude = lat,
                            longitude = lng,
                            accuracyMeters = 10.0,
                            timestamp = TrackTimestamp.now(),
                            address = address,
                        )
                    repository.updateOrderLocation(orderId, newGeoLocation)

                    val newTrackingLocation =
                        TrackingLocation(
                            address = address,
                            lat = lat,
                            lng = lng,
                        )
                    repository.updateTrackingLocation(
                        orderId = orderId,
                        newLocation = newTrackingLocation,
                        driverId = _currentDriverId.value,
                    )
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to update location: ${e.message}"
                }
            }
        }

        fun clearError() {
            _errorMessage.value = null
        }
    }

