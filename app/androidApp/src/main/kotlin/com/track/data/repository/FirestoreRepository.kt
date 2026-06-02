package com.track.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.track.domain.models.GeoLocation
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.domain.models.Product
import com.track.domain.models.StaffProfile
import com.track.domain.models.TrackingLocation
import com.track.domain.models.TrackingRecord
import com.track.domain.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepository
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) {
        // ─── USERS ──────────────────────────────────────────────────────────────

        fun getUsersFlow(): Flow<List<User>> =
            callbackFlow {
                val listener =
                    firestore
                        .collection("users")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                close(error)
                                return@addSnapshotListener
                            }
                            val users =
                                snapshot?.documents?.mapNotNull {
                                    it.toObject(User::class.java)
                                } ?: emptyList()
                            trySend(users)
                        }
                awaitClose { listener.remove() }
            }

        fun getStaffUsersFlow(): Flow<List<User>> =
            callbackFlow {
                val listener =
                    firestore
                        .collection("users")
                        .whereEqualTo("role", "STAFF")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                close(error)
                                return@addSnapshotListener
                            }
                            val users =
                                snapshot?.documents?.mapNotNull {
                                    it.toObject(User::class.java)
                                } ?: emptyList()
                            trySend(users)
                        }
                awaitClose { listener.remove() }
            }

        suspend fun getUserById(userId: String): User? =
            firestore
                .collection("users")
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java)

        suspend fun createUser(user: User): String {
            val doc = firestore.collection("users").document()
            doc.set(user).await()
            return doc.id
        }

        suspend fun updateUserActiveStatus(
            userId: String,
            isActive: Boolean,
        ) {
            firestore
                .collection("users")
                .document(userId)
                .update("isActive", isActive)
                .await()
        }

        // ─── PRODUCTS ───────────────────────────────────────────────────────────

        fun getProductsFlow(): Flow<List<Product>> =
            callbackFlow {
                val listener =
                    firestore
                        .collection("products")
                        .whereEqualTo("isActive", true)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                close(error)
                                return@addSnapshotListener
                            }
                            val products =
                                snapshot?.documents?.mapNotNull {
                                    it.toObject(Product::class.java)
                                } ?: emptyList()
                            trySend(products)
                        }
                awaitClose { listener.remove() }
            }

        suspend fun getProductById(productId: String): Product? =
            firestore
                .collection("products")
                .document(productId)
                .get()
                .await()
                .toObject(Product::class.java)

        suspend fun createProduct(product: Product): String {
            val doc = firestore.collection("products").document()
            doc.set(product).await()
            return doc.id
        }

        // ─── STAFF PROFILES ─────────────────────────────────────────────────────

        fun getStaffProfilesFlow(): Flow<List<StaffProfile>> =
            callbackFlow {
                val listener =
                    firestore
                        .collection("staff")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                close(error)
                                return@addSnapshotListener
                            }
                            val profiles =
                                snapshot?.documents?.mapNotNull {
                                    it.toObject(StaffProfile::class.java)
                                } ?: emptyList()
                            trySend(profiles)
                        }
                awaitClose { listener.remove() }
            }

        suspend fun updateStaffActiveStatus(
            staffDocId: String,
            isActive: Boolean,
        ) {
            firestore
                .collection("staff")
                .document(staffDocId)
                .update("isActive", isActive)
                .await()
        }

        // ─── ORDERS ─────────────────────────────────────────────────────────────

        fun getOrdersFlow(): Flow<List<Order>> =
            callbackFlow {
                val listener =
                    firestore
                        .collection("orders")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                close(error)
                                return@addSnapshotListener
                            }
                            val orders =
                                snapshot?.documents?.mapNotNull {
                                    it.toObject(Order::class.java)
                                } ?: emptyList()
                            trySend(orders)
                        }
                awaitClose { listener.remove() }
            }

        fun getOrdersByCustomerFlow(customerId: String): Flow<List<Order>> =
            callbackFlow {
                val listener =
                    firestore
                        .collection("orders")
                        .whereEqualTo("customerId", customerId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                close(error)
                                return@addSnapshotListener
                            }
                            val orders =
                                snapshot?.documents?.mapNotNull {
                                    it.toObject(Order::class.java)
                                } ?: emptyList()
                            trySend(orders)
                        }
                awaitClose { listener.remove() }
            }

        fun getOrdersByDriverFlow(driverId: String): Flow<List<Order>> =
            callbackFlow {
                val listener =
                    firestore
                        .collection("orders")
                        .whereEqualTo("driverId", driverId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                close(error)
                                return@addSnapshotListener
                            }
                            val orders =
                                snapshot?.documents?.mapNotNull {
                                    it.toObject(Order::class.java)
                                } ?: emptyList()
                            trySend(orders)
                        }
                awaitClose { listener.remove() }
            }

        suspend fun getOrderById(orderId: String): Order? =
            firestore
                .collection("orders")
                .document(orderId)
                .get()
                .await()
                .toObject(Order::class.java)

        suspend fun createOrder(order: Order): String {
            val doc = firestore.collection("orders").document()
            doc.set(order).await()
            return doc.id
        }

        suspend fun updateOrderStatus(
            orderId: String,
            newStatus: OrderStatus,
        ) {
            firestore
                .collection("orders")
                .document(orderId)
                .update(
                    mapOf(
                        "orderStatus" to newStatus.name,
                        "updatedAt" to Timestamp.now(),
                    ),
                ).await()
        }

        suspend fun updateOrderLocation(
            orderId: String,
            newLocation: GeoLocation,
        ) {
            firestore
                .collection("orders")
                .document(orderId)
                .update(
                    mapOf(
                        "currentLocation" to newLocation,
                        "updatedAt" to Timestamp.now(),
                    ),
                ).await()
        }

        // ─── TRACKING ───────────────────────────────────────────────────────────

        fun getTrackingRecordFlow(orderId: String): Flow<TrackingRecord?> =
            callbackFlow {
                val listener =
                    firestore
                        .collection("tracking")
                        .document(orderId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                close(error)
                                return@addSnapshotListener
                            }
                            val record = snapshot?.toObject(TrackingRecord::class.java)
                            trySend(record)
                        }
                awaitClose { listener.remove() }
            }

        suspend fun updateTrackingLocation(
            orderId: String,
            newLocation: TrackingLocation,
            driverId: String,
        ) {
            firestore
                .collection("tracking")
                .document(orderId)
                .set(
                    mapOf(
                        "currentLocation" to
                            mapOf(
                                "address" to newLocation.address,
                                "lat" to newLocation.lat,
                                "lng" to newLocation.lng,
                            ),
                        "driverId" to driverId,
                        "lastUpdated" to Timestamp.now(),
                    ),
                ).await()
        }
    }
