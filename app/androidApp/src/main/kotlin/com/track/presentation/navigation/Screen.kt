package com.track.presentation.navigation

import com.track.domain.models.UserRole

sealed class Screen(
    val route: String,
) {
    object Login : Screen("login")

    object AdminDashboard : Screen("admin_dashboard")

    object StaffDashboard : Screen("staff_dashboard")

    object DriverDashboard : Screen("driver_dashboard")

    object UserDashboard : Screen("user_dashboard")

    object Cart : Screen("cart")
}
