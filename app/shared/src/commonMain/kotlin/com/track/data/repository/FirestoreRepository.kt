package com.track.data.repository

import com.track.models.GeoLocation
import com.track.models.Order
import com.track.models.OrderStatus
import com.track.models.Product
import com.track.models.StaffProfile
import com.track.models.TrackingLocation
import com.track.models.TrackingRecord
import com.track.models.User
import com.track.models.Category
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    fun getCategoriesFlow(): Flow<List<Category>>
    fun getOrdersFlow(): Flow<List<Order>>
    fun getOrdersByCustomerFlow(customerId: String): Flow<List<Order>>
    fun getOrdersByDriverFlow(driverId: String): Flow<List<Order>>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus)
    suspend fun updateOrderLocation(orderId: String, newLocation: GeoLocation)
    fun getTrackingRecordFlow(orderId: String): Flow<TrackingRecord?>
    suspend fun updateTrackingLocation(orderId: String, newLocation: TrackingLocation, driverId: String)
    fun getUsersFlow(): Flow<List<User>>
    fun getStaffUsersFlow(): Flow<List<User>>
    fun getStaffProfilesFlow(): Flow<List<StaffProfile>>
    fun getProductsFlow(): Flow<List<Product>>
    suspend fun createOrder(order: Order): String
    suspend fun createProduct(product: Product): String
    suspend fun createUser(user: User): String
    suspend fun updateUserActiveStatus(userId: String, isActive: Boolean)
    suspend fun updateUserOnlineStatus(userId: String, isOnline: Boolean)
    suspend fun updateStaffActiveStatus(staffId: String, isActive: Boolean)
    suspend fun updateProductStock(productId: String, newStock: Int)
    suspend fun seedProducts(products: List<Product>)
    suspend fun getOrderById(orderId: String): Order?
    suspend fun getUser(userId: String): User?
    suspend fun updateUserProfile(userId: String, name: String, phone: String, shippingAddress: String, dob: String = "", country: String = "")
}

