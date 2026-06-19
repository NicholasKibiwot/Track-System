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
import com.track.presentation.admin.SuperAdminViewModel
import com.track.presentation.driver.DriverViewModel
import com.track.presentation.staff.StaffViewModel
import java.util.Calendar
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
import com.track.staff.presentation.auth.TemporaryLoginScreen

@Composable
fun StaffNavHost(
    authViewModel: AuthViewModel = hiltViewModel(),
    adminViewModel: SuperAdminViewModel = hiltViewModel(),
    staffViewModel: StaffViewModel = hiltViewModel(),
    driverViewModel: DriverViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    val onLogout = {
        // Redirect back to debug portal instead of real login
        navController.navigate("temp_login") {
            popUpTo(0) { inclusive = true }
        }
    }

    // Auto-logout simulation at Close of Business (e.g., 6 PM)
    LaunchedEffect(currentUser) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if ((hour >= 18 || hour < 8) && currentUser != null) {
            // It's after hours. In a real app, we might check if they've already been logged out today
            // or if this is the first time they open the app after 6 PM.
            // For now, we simulate the 'end of day' reset.
            // authViewModel.logout() // Uncomment for real enforcement
            // onLogout()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "temp_login",
    ) {
        composable("temp_login") {
            TemporaryLoginScreen(
                onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) },
                onNavigateToStaff = { navController.navigate(Screen.StaffDashboard.route) },
                onNavigateToDriver = { navController.navigate(Screen.DriverDashboard.route) }
            )
        }

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

        addAdminRoutes(adminViewModel, onLogout)
        addStaffRoutes(staffViewModel, onLogout)
        addDriverRoutes(navController, driverViewModel, onLogout)
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

private fun NavGraphBuilder.addAdminRoutes(
    viewModel: SuperAdminViewModel,
    onLogout: () -> Unit
) {
    composable(Screen.AdminDashboard.route) { 
        AdminDashboard(viewModel = viewModel, onLogout = onLogout)
    }
    composable(Screen.AdminOrders.route) { 
        OrderManagementScreen(viewModel = viewModel) 
    }
    composable(Screen.AdminStaff.route) { 
        StaffManagementScreen(viewModel = viewModel) 
    }
    composable(Screen.AdminProducts.route) { 
        InventoryManagementScreen(viewModel = viewModel) 
    }
}

private fun NavGraphBuilder.addStaffRoutes(
    viewModel: StaffViewModel,
    onLogout: () -> Unit
) {
    composable(Screen.StaffDashboard.route) { 
        StaffDashboard(viewModel = viewModel, onLogout = onLogout) 
    }
    composable(Screen.StaffOrderLookup.route) { 
        OrderLookupScreen(viewModel = viewModel, trackingId = null) 
    }
}

private fun NavGraphBuilder.addDriverRoutes(
    navController: NavHostController,
    viewModel: DriverViewModel,
    onLogout: () -> Unit
) {
    composable(Screen.DriverDashboard.route) {
        DriverDashboard(
            viewModel = viewModel,
            onScanPackage = { navController.navigate(Screen.DriverScan.route) },
            onLogout = onLogout
        )
    }
    composable(Screen.DriverScan.route) {
        ScanPackageScreen(
            onScanSuccess = { _ -> /* Logic */ },
            onBackClick = { navController.popBackStack() }
        )
    }
}
