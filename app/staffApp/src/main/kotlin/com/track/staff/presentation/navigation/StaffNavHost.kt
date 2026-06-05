package com.track.staff.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.auth.StaffLoginScreen
import com.track.presentation.navigation.Screen
import com.track.presentation.admin.AdminDashboard
import com.track.presentation.admin.InventoryManagementScreen
import com.track.presentation.admin.OrderManagementScreen
import com.track.presentation.admin.StaffManagementScreen
import com.track.presentation.driver.DriverDashboard
import com.track.presentation.driver.ScanPackageScreen
import com.track.presentation.staff.OrderLookupScreen
import com.track.presentation.staff.StaffDashboard
import com.track.staff.presentation.viewmodel.StaffAuthViewModel

@Composable
fun StaffNavHost(
    authViewModel: StaffAuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) Screen.StaffLogin.route else getStartDestination(currentUser?.role?.name),
    ) {
        composable(Screen.StaffLogin.route) {
            StaffLoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { role ->
                    navController.navigate(getStartDestination(role)) {
                        popUpTo(Screen.StaffLogin.route) { inclusive = true }
                    }
                },
                onBackClick = { /* No back for staff app root login */ }
            )
        }

        addAdminRoutes()
        addStaffRoutes()
        addDriverRoutes(navController)
    }
}

private fun getStartDestination(role: String?): String {
    return when (role) {
        "SUPER_ADMIN" -> Screen.AdminDashboard.route
        "STAFF" -> Screen.StaffDashboard.route
        "DRIVER" -> Screen.DriverDashboard.route
        else -> Screen.StaffLogin.route
    }
}

private fun NavGraphBuilder.addAdminRoutes() {
    composable(Screen.AdminDashboard.route) { AdminDashboard() }
    composable(Screen.AdminOrders.route) { OrderManagementScreen() }
    composable(Screen.AdminStaff.route) { StaffManagementScreen() }
    composable(Screen.AdminProducts.route) { InventoryManagementScreen() }
}

private fun NavGraphBuilder.addStaffRoutes() {
    composable(Screen.StaffDashboard.route) { StaffDashboard() }
    composable(Screen.StaffOrderLookup.route) { OrderLookupScreen(trackingId = null) }
}

private fun NavGraphBuilder.addDriverRoutes(navController: NavHostController) {
    composable(Screen.DriverDashboard.route) { DriverDashboard() }
    composable(Screen.DriverScan.route) {
        ScanPackageScreen(
            onScanSuccess = { _ -> /* Logic */ },
            onBackClick = { navController.popBackStack() }
        )
    }
}
