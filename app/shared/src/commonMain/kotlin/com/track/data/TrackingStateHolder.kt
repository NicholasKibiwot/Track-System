package com.track.data

import com.track.data.repository.FirestoreRepository
import com.track.domain.models.GeoLocation
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.domain.models.TrackingLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingStateHolder
    @Inject
    constructor(
        private val repository: FirestoreRepository,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private val _orders = MutableStateFlow<List<Order>>(emptyList())
        val orders: StateFlow<List<Order>> = _orders.asStateFlow()

        init {
            scope.launch {
                repository.getOrdersFlow().collect { _orders.value = it }
            }
        }

        fun getOrderById(orderId: String): Order? = _orders.value.find { it.id == orderId }

        fun updateOrderStatus(
            orderId: String,
            newStatus: OrderStatus,
        ) {
            scope.launch {
                repository.updateOrderStatus(orderId, newStatus)
            }
        }

        fun updateOrderLocation(
            orderId: String,
            newLocation: GeoLocation,
        ) {
            scope.launch {
                repository.updateOrderLocation(orderId, newLocation)
            }
        }

        fun updateTrackingRecord(
            orderId: String,
            driverId: String,
            newLocation: TrackingLocation,
        ) {
            scope.launch {
                repository.updateTrackingLocation(orderId, newLocation, driverId)
            }
        }

        fun simulateDriverMovement(
            orderId: String,
            driverId: String,
        ) {
            val order = getOrderById(orderId) ?: return
            val current = order.currentLocation ?: return

            val nextLat = current.latitude + kotlin.random.Random.nextDouble(-0.008, 0.008)
            val nextLon = current.longitude + kotlin.random.Random.nextDouble(-0.008, 0.008)

            val newGeoLocation =
                GeoLocation(
                    id = "LOC-${System.currentTimeMillis()}",
                    latitude = nextLat,
                    longitude = nextLon,
                    accuracyMeters = kotlin.random.Random.nextDouble(5.0, 20.0),
                    address = "En route to ${order.destination}",
                )

            val newTrackingLocation =
                TrackingLocation(
                    address = newGeoLocation.address,
                    lat = nextLat,
                    lng = nextLon,
                )

            // Update both the orders collection and the tracking collection
            updateOrderLocation(orderId, newGeoLocation)
            updateTrackingRecord(orderId, driverId, newTrackingLocation)
        }
    }
