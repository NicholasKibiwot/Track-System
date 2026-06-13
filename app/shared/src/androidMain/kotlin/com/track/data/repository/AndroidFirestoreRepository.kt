package com.track.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.track.domain.models.GeoLocation
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.domain.models.Product
import com.track.domain.models.StaffProfile
import com.track.domain.models.TrackingLocation
import com.track.domain.models.TrackingRecord
import com.track.domain.models.User
import com.track.domain.models.UserRole
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidFirestoreRepository @Inject constructor(
    private val db: FirebaseFirestore,
) : FirestoreRepository {
    
    override fun getOrdersFlow(): Flow<List<Order>> = callbackFlow {
        val listener = db.collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    override fun getOrdersByCustomerFlow(customerId: String): Flow<List<Order>> = callbackFlow {
        val listener = db.collection("orders")
            .whereEqualTo("customerId", customerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    override fun getOrdersByDriverFlow(driverId: String): Flow<List<Order>> = callbackFlow {
        val listener = db.collection("orders")
            .whereEqualTo("driverId", driverId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        db.collection("orders").document(orderId)
            .update("orderStatus", status.name, "updatedAt", Timestamp.now())
            .await()
    }

    override suspend fun updateOrderLocation(orderId: String, newLocation: GeoLocation) {
        db.collection("orders").document(orderId)
            .update(
                "currentLocation", newLocation,
                "locationHistory", FieldValue.arrayUnion(newLocation),
                "updatedAt", Timestamp.now()
            ).await()
    }

    override fun getTrackingRecordFlow(orderId: String): Flow<TrackingRecord?> = callbackFlow {
        val listener = db.collection("tracking").document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(TrackingRecord::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateTrackingLocation(orderId: String, newLocation: TrackingLocation, driverId: String) {
        db.collection("tracking").document(orderId)
            .set(
                mapOf(
                    "orderId" to orderId,
                    "currentLocation" to newLocation,
                    "driverId" to driverId,
                    "lastUpdated" to Timestamp.now(),
                    "locationHistory" to FieldValue.arrayUnion("${newLocation.lat},${newLocation.lng}")
                ),
                SetOptions.merge()
            ).await()
    }

    override fun getUsersFlow(): Flow<List<User>> = callbackFlow {
        val listener = db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(User::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override fun getStaffUsersFlow(): Flow<List<User>> = callbackFlow {
        val listener = db.collection("users")
            .whereIn("role", listOf(UserRole.STAFF.name, UserRole.DRIVER.name))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(User::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override fun getStaffProfilesFlow(): Flow<List<StaffProfile>> = callbackFlow {
        val listener = db.collection("staff")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(StaffProfile::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override fun getProductsFlow(): Flow<List<Product>> = callbackFlow {
        val listener = db.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(Product::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createOrder(order: Order): String {
        val docRef = if (order.id.isBlank()) db.collection("orders").document() else db.collection("orders").document(order.id)
        val finalOrder = if (order.id.isBlank()) order.copy(id = docRef.id) else order
        docRef.set(finalOrder).await()
        return docRef.id
    }

    override suspend fun createProduct(product: Product): String {
        val docRef = if (product.id.isBlank()) db.collection("products").document() else db.collection("products").document(product.id)
        val finalProduct = if (product.id.isBlank()) product.copy(id = docRef.id) else product
        docRef.set(finalProduct).await()
        return docRef.id
    }

    override suspend fun createUser(user: User): String {
        val docRef = if (user.id.isBlank()) db.collection("users").document() else db.collection("users").document(user.id)
        val finalUser = if (user.id.isBlank()) user.copy(id = docRef.id) else user
        docRef.set(finalUser).await()
        return docRef.id
    }

    override suspend fun updateUserActiveStatus(userId: String, isActive: Boolean) {
        db.collection("users").document(userId).update("isActive", isActive).await()
    }

    override suspend fun updateUserOnlineStatus(userId: String, isOnline: Boolean) {
        db.collection("users").document(userId).update("isOnline", isOnline).await()
    }

    override suspend fun updateStaffActiveStatus(staffId: String, isActive: Boolean) {
        db.collection("staff").document(staffId).update("isActive", isActive).await()
    }

    override suspend fun updateProductStock(productId: String, newStock: Int) {
        db.collection("products").document(productId).update("stock", newStock).await()
    }

    override suspend fun getOrderById(orderId: String): Order? = try {
        db.collection("orders").document(orderId).get().await().toObject(Order::class.java)
    } catch (_: Exception) {
        null
    }

    override suspend fun getUser(userId: String): User? = try {
        db.collection("users").document(userId).get().await().toObject(User::class.java)
    } catch (_: Exception) {
        null
    }

    override suspend fun updateUserProfile(userId: String, name: String, phone: String, shippingAddress: String, dob: String, country: String) {
        db.collection("users").document(userId).update(
            mapOf(
                "displayName" to name,
                "phoneNumber" to phone,
                "shippingAddress" to shippingAddress,
                "dob" to dob,
                "country" to country
            )
        ).await()
    }
}
