package com.track.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.track.presentation.admin.AdminAddProductScreen
import com.track.presentation.admin.AdminAddUserScreen
import com.track.presentation.admin.SuperAdminDashboard
import com.track.presentation.admin.SuperAdminViewModel
import com.track.presentation.auth.AuthViewModel

@Composable
fun AdminNavGraph(
    navController: NavHostController,
    adminViewModel: SuperAdminViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AdminDashboard.route
    ) {
        composable(Screen.AdminDashboard.route) {
            SuperAdminDashboard(
                viewModel = adminViewModel,
                onAddProductClick = { navController.navigate(Screen.AdminAddProduct.route) },
                onAddStaffClick = { navController.navigate(Screen.AdminAddUser.route) }
            )
        }

        composable(Screen.AdminAddProduct.route) {
            AdminAddProductScreen(
                viewModel = adminViewModel,
                onBackClick = { navController.popBackStack() },
                onProductAdded = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminAddUser.route) {
            AdminAddUserScreen(
                viewModel = adminViewModel,
                onBackClick = { navController.popBackStack() },
                onUserAdded = { navController.popBackStack() }
            )
        }
    }
}
