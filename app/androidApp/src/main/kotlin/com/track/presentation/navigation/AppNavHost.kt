package com.track.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.track.domain.models.UserRole

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            com.track.presentation.auth.LoginScreen(
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        UserRole.SUPER_ADMIN -> Screen.AdminDashboard.route
                        UserRole.STAFF -> Screen.StaffDashboard.route
                        UserRole.DRIVER -> Screen.DriverDashboard.route
                        UserRole.CUSTOMER -> Screen.UserDashboard.route
                        else -> Screen.Login.route
                    }

                    if (destination != Screen.Login.route) {
                        navController.navigate(destination) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.AdminDashboard.route) {
            com.track.presentation.admin.AdminDashboard()
        }

        composable(Screen.StaffDashboard.route) {
            com.track.presentation.staff.StaffDashboard()
        }

        composable(Screen.DriverDashboard.route) {
            com.track.presentation.driver.DriverDashboard()
        }

        composable(Screen.UserDashboard.route) {
            com.track.presentation.user.UserDashboard()
        }
    }
}