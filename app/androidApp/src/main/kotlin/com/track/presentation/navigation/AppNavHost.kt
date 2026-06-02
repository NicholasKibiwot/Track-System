package com.track.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.track.data.FakeData
import com.track.domain.models.UserRole
import com.track.presentation.admin.AdminDashboard
import com.track.presentation.auth.LoginScreen
import com.track.presentation.home.HomeScreen
import com.track.presentation.customer.CartScreen
import com.track.presentation.customer.CheckoutScreen
import com.track.presentation.staff.StaffDashboard
import com.track.presentation.user.UserDashboard
import com.track.presentation.tracking.LiveTrackingScreen
import com.track.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(val authRepository: AuthRepository) : ViewModel()

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authRepository = authViewModel.authRepository

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCart = { 
                    if (authRepository.isAuthenticated()) {
                        navController.navigate(Screen.Cart.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                onNavigateToProduct = { /* Product Detail */ },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role: UserRole ->
                    val destination = when (role) {
                        UserRole.SUPER_ADMIN -> Screen.AdminDashboard.route
                        UserRole.STAFF -> Screen.StaffDashboard.route
                        UserRole.DRIVER -> Screen.DriverDashboard.route
                        UserRole.CUSTOMER -> Screen.Home.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboard()
        }

        composable(Screen.StaffDashboard.route) {
            StaffDashboard()
        }

        composable(Screen.DriverDashboard.route) {
            UserDashboard() // Driver view
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                }
            )
        }

        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onOrderSuccess = { orderId ->
                    navController.navigate(Screen.Tracking(orderId).route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("tracking_screen/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            val order = FakeData.getOrderById(orderId)

            if (order != null) {
                LiveTrackingScreen(
                    order = order,
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    }
}
