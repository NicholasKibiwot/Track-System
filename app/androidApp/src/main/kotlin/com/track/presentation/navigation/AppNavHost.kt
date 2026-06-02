package com.track.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.track.presentation.admin.AdminDashboard
import com.track.presentation.admin.OrderManagementScreen
import com.track.presentation.admin.StaffManagementScreen
import com.track.presentation.admin.InventoryManagementScreen
import com.track.presentation.auth.LoginScreen
import com.track.presentation.customer.CartScreen
import com.track.presentation.customer.CheckoutScreen
import com.track.presentation.home.HomeScreen
import com.track.presentation.driver.DriverDashboard
import com.track.presentation.driver.ScanPackageScreen
import com.track.presentation.staff.StaffDashboard
import com.track.presentation.staff.OrderLookupScreen
import com.track.presentation.auth.AuthViewModel

@Composable
fun AppNavHost(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        // ── PUBLIC: Home / Product Browse ─────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCart = {
                    if (currentUser != null) {
                        navController.navigate(Screen.Cart.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToMyOrders = {
                    if (currentUser != null) {
                        navController.navigate(Screen.MyOrders.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
            )
        }

        // ── AUTH: Login ───────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination =
                        when (role) {
                            "SUPER_ADMIN" -> Screen.AdminDashboard.route
                            "STAFF" -> Screen.StaffDashboard.route
                            "DRIVER" -> Screen.DriverDashboard.route
                            else -> Screen.Home.route // CUSTOMER
                        }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
            )
        }

        // ── CUSTOMER: Cart ────────────────────────────────────────────────────
        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                },
                onBackClick = { navController.popBackStack() },
            )
        }

        // ── CUSTOMER: Checkout ────────────────────────────────────────────────
        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onOrderSuccess = { orderId ->
                    navController.navigate("tracking/$orderId") {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
            )
        }

        // ── CUSTOMER: My Orders ───────────────────────────────────────────────
        composable(Screen.MyOrders.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("My Orders Screen (Coming Soon)")
            }
        }

        // ── CUSTOMER: Order Tracking ──────────────────────────────────────────
        composable(
            route = "tracking/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tracking Order: $orderId (Coming Soon)")
            }
        }

        // ── SUPER ADMIN: Dashboard ─────────────────────────────────────────────
        composable(Screen.AdminDashboard.route) {
            AdminDashboard()
        }

        composable(Screen.AdminOrders.route) {
            OrderManagementScreen()
        }

        composable(Screen.AdminStaff.route) {
            StaffManagementScreen()
        }

        composable(Screen.AdminProducts.route) {
            InventoryManagementScreen()
        }

        // ── STAFF: Dashboard ──────────────────────────────────────────────────
        composable(Screen.StaffDashboard.route) {
            StaffDashboard()
        }

        composable(Screen.StaffOrderLookup.route) {
            OrderLookupScreen(trackingId = null)
        }

        // ── DRIVER: Dashboard ─────────────────────────────────────────────────
        composable(Screen.DriverDashboard.route) {
            DriverDashboard()
        }

        composable(Screen.DriverScan.route) {
            ScanPackageScreen(onScanSuccess = { /* Handle scan */ })
        }
    }
}
