package com.track.presentation.navigation

sealed class Screen(
    val route: String,
) {
    // Public (no login required)
    object Home : Screen("home")

    object Login : Screen("login")

    object Register : Screen("register")

    object StaffLogin : Screen("staff_login")

    // Customer (login required)
    object Cart : Screen("cart")

    object Checkout : Screen("checkout")

    object MyOrders : Screen("my_orders")

    data class Tracking(
        val orderId: String,
    ) : Screen("tracking/{orderId}") {
        val resolvedRoute get() = "tracking/$orderId"
    }

    // Super Admin
    object AdminDashboard : Screen("admin_dashboard")

    object AdminOrders : Screen("admin_orders")

    object AdminStaff : Screen("admin_staff")

    object AdminProducts : Screen("admin_products")

    object AdminAddProduct : Screen("admin_add_product")

    // Staff
    object StaffDashboard : Screen("staff_dashboard")

    object StaffOrderLookup : Screen("staff_order_lookup")

    object StaffScanPackage : Screen("staff_scan_package")

    // Driver
    object DriverDashboard : Screen("driver_dashboard")

    object DriverScan : Screen("driver_scan")
}
