package com.track.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.driver.DriverDashboard
import com.track.presentation.driver.DriverViewModel
import com.track.presentation.driver.ScanPackageScreen
import com.track.util.kmpViewModel

@Composable
fun DriverNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    showMessage: (String) -> Unit
) {
    val driverViewModel = kmpViewModel<DriverViewModel>()
    
    NavHost(
        navController = navController,
        startDestination = Screen.DriverDashboard.route
    ) {
        composable(Screen.DriverDashboard.route) {
            DriverDashboard(
                viewModel = driverViewModel,
                onScanPackage = { navController.navigate(Screen.DriverScan.route) },
                onLogout = {
                    authViewModel.logout()
                    showMessage("Logged out successfully")
                }
            )
        }

        composable(Screen.DriverScan.route) {
            ScanPackageScreen(
                onScanSuccess = { _ -> navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
