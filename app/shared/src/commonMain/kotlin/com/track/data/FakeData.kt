package com.track.data

import com.track.domain.models.GeoLocation
import com.track.domain.models.Order
import com.track.domain.models.OrderItem
import com.track.domain.models.OrderStatus
import com.track.domain.models.Product
import com.track.domain.models.StaffProfile
import com.track.domain.models.TrackingLocation
import com.track.domain.models.TrackingRecord
import com.track.domain.models.User
import com.track.domain.models.UserRole

/**
 * Used ONLY for Compose @Preview functions and UI design-time tools.
 * All runtime data comes from Firestore via FirestoreRepository.
 */
object FakeData {
    val previewUser =
        User(
            id = "user_001",
            email = "customer@yhecutmedia.com",
            name = "John Doe",
            role = UserRole.CUSTOMER,
            isActive = true,
            phone = "+254712345678",
        )

    val previewAdminUser =
        User(
            id = "admin_001",
            email = "admin@yhecutmedia.com",
            name = "Super Admin",
            role = UserRole.SUPER_ADMIN,
            isActive = true,
            phone = "",
        )

    val previewStaffUser =
        User(
            id = "staff_001",
            email = "staff@yhecutmedia.com",
            name = "Demo Staff",
            role = UserRole.STAFF,
            isActive = false,
            phone = "",
        )

    val previewDriverUser =
        User(
            id = "driver_001",
            email = "driver@yhecutmedia.com",
            name = "Demo Driver",
            role = UserRole.DRIVER,
            isActive = true,
            phone = "",
        )

    val previewStaffProfile =
        StaffProfile(
            id = "staff_001",
            userId = "user_001",
            employeeId = "EMP-001",
            department = "Delivery",
            office = "Nairobi HQ",
            hiredBy = "admin_001",
            isActive = true,
        )

    val previewProduct =
        Product(
            id = "prod_001",
            name = "Heidelberg XL 75 Plus",
            description = "Large format billboard printer",
            price = 450000.0,
            imageUrl = "https://yhecutmedia.com/images/heidelberg-xl75.jpg",
            stock = 5,
            category = "billboard",
            isActive = true,
            addedBy = "user_001",
        )

    val previewProducts =
        listOf(
            previewProduct,
            Product(
                id = "prod_002",
                name = "Desktop Cutter Pro",
                description = "Precision vinyl cutter",
                price = 1200.0,
                imageUrl = "",
                stock = 25,
                category = "TOOLS",
                isActive = true,
                addedBy = "user_001",
            ),
            Product(
                id = "prod_003",
                name = "Large Format Ink",
                description = "Eco-solvent ink cartridge",
                price = 85.0,
                imageUrl = "",
                stock = 100,
                category = "CONSUMABLES",
                isActive = true,
                addedBy = "user_001",
            ),
        )

    val previewOrder =
        Order(
            id = "order_001",
            trackingNumber = "TRK-9921",
            customerId = "user_001",
            customerName = "John Doe",
            items =
                listOf(
                    OrderItem(
                        id = "item_001",
                        productId = "prod_001",
                        productName = "Heidelberg XL 75 Plus",
                        quantity = 1,
                        unitPrice = 450000.0,
                        imageUrl = null,
                    ),
                ),
            totalAmount = 450000.0,
            paymentMethod = "MOMO",
            paymentStatus = "PAID",
            orderStatus = OrderStatus.INTRANSIT,
            deliveryType = "COMPANY",
            driverId = "driver_user_001",
            driverName = "Demo Driver",
            origin = "Nairobi HQ",
            destination = "Mombasa Branch",
            currentLocation =
                GeoLocation(
                    id = "LOC-1",
                    latitude = -1.2921,
                    longitude = 36.8219,
                    accuracyMeters = 10.0,
                    address = "Nairobi, Kenya",
                ),
            locationHistory = emptyList(),
            createdAtMillis = 1686300000000L,
            updatedAtMillis = 1686300000000L,
        )

    val previewOrders =
        listOf(
            previewOrder,
            Order(
                id = "order_002",
                trackingNumber = "TRK-8842",
                customerId = "user_002",
                customerName = "Jane Smith",
                items =
                    listOf(
                        OrderItem(
                            id = "item_002",
                            productId = "prod_002",
                            productName = "Desktop Cutter Pro",
                            quantity = 2,
                            unitPrice = 1200.0,
                            imageUrl = null,
                        ),
                    ),
                totalAmount = 2400.0,
                paymentMethod = "CARD",
                paymentStatus = "PENDING",
                orderStatus = OrderStatus.PROCESSING,
                deliveryType = "OUTSOURCED",
                driverId = null,
                driverName = null,
                origin = "Nakuru Branch",
                destination = "Nairobi Hub",
                currentLocation =
                    GeoLocation(
                        id = "LOC-2",
                        latitude = -0.3031,
                        longitude = 36.0800,
                        accuracyMeters = 5.0,
                        address = "Nakuru Town",
                    ),
                locationHistory = emptyList(),
                createdAtMillis = 1686300000000L,
                updatedAtMillis = 1686300000000L,
            ),
        )

    val previewTrackingRecord =
        TrackingRecord(
            orderId = "order_001",
            currentLocation =
                TrackingLocation(
                    address = "Nairobi, Kenya",
                    lat = -1.2921,
                    lng = 36.8219,
                ),
            driverId = "driver_user_001",
            locationHistory = emptyList(),
        )

    fun getOrderById(id: String): Order? = previewOrders.find { it.id == id }
}
