package com.track.presentation.navigation

sealed class Screen(
    val route: String,
) {
    object Home : Screen("home")

    object Login : Screen("login")

    object AdminDashboard : Screen("admin_dashboard")

    object StaffDashboard : Screen("staff_dashboard")

    object DriverDashboard : Screen("driver_dashboard")

    object Cart : Screen("cart")

    object Checkout : Screen("checkout")

    object Inventory : Screen("inventory")

    object PickupStations : Screen("pickup_stations")

    data class Tracking(val orderId: String) : Screen("tracking_screen/$orderId")
}
