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
import com.track.staff.presentation.viewmodel.AppAuthViewModel
import com.track.staff.presentation.viewmodel.AppDriverViewModel
import com.track.staff.presentation.viewmodel.AppStaffViewModel
import com.track.staff.presentation.viewmodel.AppSuperAdminViewModel

@Composable
fun StaffNavHost(
    authViewModel: AppAuthViewModel = hiltViewModel(),
    adminViewModel: AppSuperAdminViewModel = hiltViewModel(),
    staffViewModel: AppStaffViewModel = hiltViewModel(),
    driverViewModel: AppDriverViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    
    val onLogout = {
        navController.navigate(Screen.StaffLogin.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.StaffLogin.route,
    ) {
        composable(Screen.StaffLogin.route) {
            StaffLoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { role ->
                    navController.navigate(getStartDestination(role)) {
                        popUpTo(Screen.StaffLogin.route) { inclusive = true }
                    }
                },
                onBackClick = { /* Root */ }
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
