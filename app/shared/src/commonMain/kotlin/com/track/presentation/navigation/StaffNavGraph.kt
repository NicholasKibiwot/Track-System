package com.track.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.staff.StaffDashboard
import com.track.presentation.staff.StaffViewModel
import com.track.util.kmpViewModel

@Composable
fun StaffNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    showMessage: (String) -> Unit
) {
    val staffViewModel = kmpViewModel<StaffViewModel>()
    
    NavHost(
        navController = navController,
        startDestination = Screen.StaffDashboard.route
    ) {
        composable(Screen.StaffDashboard.route) {
            StaffDashboard(
                viewModel = staffViewModel,
                onLogout = {
                    authViewModel.logout()
                    showMessage("Logged out successfully")
                }
            )
        }
    }
}
