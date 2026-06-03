package com.track.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.track.domain.models.User
import com.track.domain.models.UserRole
import com.track.presentation.admin.AdminDashboard
import com.track.presentation.admin.InventoryManagementScreen
import com.track.presentation.admin.OrderManagementScreen
import com.track.presentation.admin.StaffManagementScreen
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.auth.LoginScreen
import com.track.presentation.auth.RegisterScreen
import com.track.presentation.auth.StaffLoginScreen
import com.track.presentation.customer.CartScreen
import com.track.presentation.customer.CheckoutScreen
import com.track.presentation.customer.CustomerViewModel
import com.track.presentation.driver.DriverDashboard
import com.track.presentation.driver.ScanPackageScreen
import com.track.presentation.home.HomeScreen
import com.track.presentation.staff.OrderLookupScreen
import com.track.presentation.staff.StaffDashboard

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel = hiltViewModel(),
    customerViewModel: CustomerViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Pass customer info to CustomerViewModel whenever user changes
    currentUser?.let { user ->
        if (user.role == UserRole.CUSTOMER) {
            customerViewModel.setCustomer(user.id, user.name)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        addPublicRoutes(navController, currentUser)
        addAuthRoutes(navController)
        addCustomerRoutes(navController, currentUser, customerViewModel)
        addAdminRoutes()
        addStaffRoutes(navController)
        addDriverRoutes(navController)
    }
}

private fun NavGraphBuilder.addPublicRoutes(navController: NavHostController, currentUser: User?) {
    composable(Screen.Home.route) {
        HomeScreen(
            onNavigateToCart = {
                if (currentUser == null) {
                    navController.navigate(Screen.Login.route)
                } else if (currentUser.role == UserRole.CUSTOMER) {
                    navController.navigate(Screen.Cart.route)
                }
            },
            onNavigateToLogin = { navController.navigate(Screen.Login.route) },
            onNavigateToMyOrders = {
                if (currentUser != null && currentUser.role == UserRole.CUSTOMER) {
                    navController.navigate(Screen.MyOrders.route)
                } else {
                    navController.navigate(Screen.Login.route)
                }
            },
        )
    }
}

private fun NavGraphBuilder.addAuthRoutes(navController: NavHostController) {
    composable(Screen.Login.route) {
        LoginScreen(
            onLoginSuccess = { _ ->
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            },
            onNavigateToRegister = { navController.navigate(Screen.Register.route) },
            onNavigateToStaffLogin = { navController.navigate(Screen.StaffLogin.route) },
            onBackClick = { navController.popBackStack() },
        )
    }

    composable(Screen.Register.route) {
        RegisterScreen(
            onRegisterSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(Screen.StaffLogin.route) {
        StaffLoginScreen(
            onLoginSuccess = { role ->
                val destination = when (role) {
                    "SUPER_ADMIN" -> Screen.AdminDashboard.route
                    "STAFF" -> Screen.StaffDashboard.route
                    "DRIVER" -> Screen.DriverDashboard.route
                    else -> Screen.Home.route
                }
                navController.navigate(destination) {
                    popUpTo(Screen.StaffLogin.route) { inclusive = true }
                }
            },
            onBackClick = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addCustomerRoutes(
    navController: NavHostController,
    currentUser: User?,
    customerViewModel: CustomerViewModel
) {
    composable(Screen.Cart.route) {
        if (currentUser?.role == UserRole.CUSTOMER) {
            CartScreen(
                onNavigateToCheckout = { navController.navigate(Screen.Checkout.route) },
                onBackClick = { navController.popBackStack() },
            )
        } else {
            navController.popBackStack()
        }
    }

    composable(Screen.Checkout.route) {
        if (currentUser?.role == UserRole.CUSTOMER) {
            CheckoutScreen(
                onOrderSuccess = { orderId ->
                    navController.navigate("tracking/$orderId") {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
            )
        } else {
            navController.popBackStack()
        }
    }

    composable(Screen.MyOrders.route) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("My Orders Screen (Coming Soon)")
        }
    }

    composable(
        route = "tracking/{orderId}",
        arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
        val order = customerViewModel.getOrderById(orderId)
        val trackingSuccess by customerViewModel.orderSuccess.collectAsState()
        
        Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (trackingSuccess != null) {
                    Text("SUCCESS!", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Tracking ID: $trackingSuccess", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    androidx.compose.material3.Button(onClick = { customerViewModel.clearOrderSuccess() }) {
                        Text("OK")
                    }
                } else {
                    Text("Tracking Order: $orderId")
                    Text("Tracking Number: ${order?.trackingNumber ?: "Loading..."}")
                }
            }
        }
    }
}

private fun NavGraphBuilder.addAdminRoutes() {
    composable(Screen.AdminDashboard.route) { AdminDashboard() }
    composable(Screen.AdminOrders.route) { OrderManagementScreen() }
    composable(Screen.AdminStaff.route) { StaffManagementScreen() }
    composable(Screen.AdminProducts.route) { InventoryManagementScreen() }
}

private fun NavGraphBuilder.addStaffRoutes(navController: NavHostController) {
    composable(Screen.StaffDashboard.route) { StaffDashboard() }
    composable(Screen.StaffOrderLookup.route) { OrderLookupScreen(trackingId = null) }
}

private fun NavGraphBuilder.addDriverRoutes(navController: NavHostController) {
    composable(Screen.DriverDashboard.route) { DriverDashboard() }
    composable(Screen.DriverScan.route) {
        ScanPackageScreen(
            onScanSuccess = { _ -> /* Handle scan logic */ },
            onBackClick = { navController.popBackStack() }
        )
    }
}
