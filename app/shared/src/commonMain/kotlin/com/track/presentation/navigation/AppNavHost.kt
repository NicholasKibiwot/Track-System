package com.track.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.track.getPlatform
import com.track.util.kmpViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.track.domain.models.User
import com.track.domain.models.UserRole
import com.track.presentation.admin.AdminAddProductScreen
import com.track.presentation.admin.AdminDashboard
import com.track.presentation.admin.WebAdminDashboard
import com.track.presentation.admin.SuperAdminViewModel
import com.track.presentation.auth.ForgotPasswordScreen
import com.track.presentation.auth.LoginScreen
import com.track.presentation.auth.RegisterScreen
import com.track.presentation.customer.*
import com.track.presentation.home.HomeScreen
import com.track.presentation.home.ModernHomeScreen
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.customer.CustomerViewModel
import com.track.presentation.staff.StaffDashboard
import com.track.presentation.staff.StaffViewModel
import com.track.presentation.welcome.WelcomeScreen
import com.track.presentation.driver.DriverDashboard
import com.track.presentation.driver.DriverViewModel
import com.track.presentation.driver.ScanPackageScreen
import com.track.presentation.staff.OrderLookupScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    customerViewModel: CustomerViewModel,
    adminViewModel: SuperAdminViewModel,
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isWeb = remember { getPlatform().isWeb }

    fun showMessage(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    currentUser?.let { user ->
        if (user.role == UserRole.CUSTOMER) {
            customerViewModel.setCustomer(user.id, user.name)
        }
    }

    // Force sign-out if current user state is null but Firebase says we are logged in
    LaunchedEffect(currentUser) {
        if (currentUser == null && !authViewModel.isAuthenticated()) {
            // Fully logged out
        }

        // Auto-redirect for Admin/Staff/Driver on Web
        if (isWeb && currentUser != null) {
            val destination = getWebRedirectDestination(currentUser?.role)
            if (destination != null) {
                navController.navigate(destination) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = getStartDestination(isWeb, currentUser),
            modifier = Modifier.padding(padding)
        ) {
            addPublicRoutes(navController, customerViewModel, authViewModel)
            addAuthRoutes(navController, authViewModel, ::showMessage)
            addCustomerRoutes(navController, currentUser, customerViewModel, authViewModel, ::showMessage)
            addAdminRoutes(navController, adminViewModel)
            addStaffRoutes(navController)
            addDriverRoutes(navController)
        }
    }
}

private fun getWebRedirectDestination(role: UserRole?): String? {
    return when (role) {
        UserRole.SUPER_ADMIN, UserRole.ADMIN -> Screen.AdminDashboard.route
        UserRole.STAFF -> Screen.StaffDashboard.route
        UserRole.DRIVER -> Screen.DriverDashboard.route
        else -> null
    }
}

private fun getStartDestination(isWeb: Boolean, currentUser: User?): String {
    if (isWeb && currentUser != null) {
        return when (currentUser.role) {
            UserRole.SUPER_ADMIN, UserRole.ADMIN -> Screen.AdminDashboard.route
            UserRole.STAFF -> Screen.StaffDashboard.route
            else -> Screen.Welcome.route
        }
    }
    return Screen.Welcome.route
}

private fun NavGraphBuilder.addDriverRoutes(
    navController: NavHostController,
) {
    composable(Screen.DriverDashboard.route) {
        val driverViewModel = kmpViewModel<DriverViewModel>()
        DriverDashboard(
            viewModel = driverViewModel,
            onScanPackage = { navController.navigate(Screen.DriverScan.route) },
            onLogout = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.DriverDashboard.route) { inclusive = true }
                }
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

private fun NavGraphBuilder.addStaffRoutes(
    navController: NavHostController,
) {
    composable(Screen.StaffDashboard.route) {
        val staffViewModel = kmpViewModel<StaffViewModel>()
        StaffDashboard(
            viewModel = staffViewModel,
            onLogout = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.StaffDashboard.route) { inclusive = true }
                }
            }
        )
    }

    composable(Screen.StaffOrderLookup.route) {
        val staffViewModel = kmpViewModel<StaffViewModel>()
        OrderLookupScreen(
            viewModel = staffViewModel,
            trackingId = null
        )
    }
}

private fun NavGraphBuilder.addAdminRoutes(
    navController: NavHostController,
    adminViewModel: SuperAdminViewModel,
) {
    composable(Screen.AdminDashboard.route) {
        if (getPlatform().isWeb) {
            WebAdminDashboard(
                viewModel = adminViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                    }
                }
            )
        } else {
            AdminDashboard(
                viewModel = adminViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }

    composable(Screen.AdminAddProduct.route) {
        AdminAddProductScreen(
            viewModel = adminViewModel,
            onBackClick = { navController.popBackStack() },
            onProductAdded = { navController.popBackStack() },
        )
    }
}

private fun NavGraphBuilder.addPublicRoutes(
    navController: NavHostController,
    customerViewModel: CustomerViewModel,
    authViewModel: AuthViewModel,
) {
    composable(Screen.Welcome.route) {
        WelcomeScreen(
            onGetStarted = { navController.navigate(Screen.Home.route) },
            onSignIn = { navController.navigate(Screen.Login.route) },
        )
    }

    composable(Screen.Home.route) {
        ModernHomeScreen(
            onNavigateToCart = { navController.navigate(Screen.Cart.route) },
            onNavigateToProfile = {
                if (authViewModel.isAuthenticated()) {
                    navController.navigate(Screen.Profile.route)
                } else {
                    navController.navigate(Screen.Login.route)
                }
            },
            onNavigateToProductDetails = { productId ->
                navController.navigate("product_details/$productId")
            },
            onNavigateToCategory = { categoryId ->
                navController.navigate("category_products/$categoryId")
            },
            viewModel = customerViewModel
        )
    }

    composable(
        route = "category_products/{categoryId}",
        arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
    ) { backStackEntry ->
        val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
        CategoryProductsScreen(
            categoryId = categoryId,
            viewModel = customerViewModel,
            onBackClick = { navController.popBackStack() },
            onProductClick = { productId -> navController.navigate("product_details/$productId") }
        )
    }

    composable(
        route = "product_details/{productId}",
        arguments = listOf(navArgument("productId") { type = NavType.StringType }),
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
        ProductDetailsScreen(
            productId = productId,
            viewModel = customerViewModel,
            onBackClick = { navController.popBackStack() },
            onAddToCart = { product -> customerViewModel.addToCart(product) },
        )
    }
}

private fun NavGraphBuilder.addAuthRoutes(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    showMessage: (String) -> Unit,
) {
    composable(Screen.Login.route) {
        CustomerLoginContainer(
            onProfileCompleted = {
                showMessage("Successfully logged in!")
                navController.navigate(Screen.Profile.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            },
            onForgotPasswordClick = { navController.navigate("forgot_password") },
            onBackClick = { navController.popBackStack() },
            authViewModel = authViewModel
        )
    }

    composable(
        route = "complete_profile/{uid}",
        arguments = listOf(navArgument("uid") { type = NavType.StringType })
    ) { backStackEntry ->
        val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
        CustomerProfileCompletionScreen(
            uid = uid,
            onCompleted = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        )
    }

    composable(Screen.Register.route) {
        RegisterScreen(
            viewModel = authViewModel,
            onRegisterSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            onBackClick = { navController.popBackStack() },
        )
    }

    composable("forgot_password") {
        ForgotPasswordScreen(
            onBackClick = { navController.popBackStack() },
            viewModel = authViewModel
        )
    }
}

private fun NavGraphBuilder.addCustomerRoutes(
    navController: NavHostController,
    currentUser: User?,
    customerViewModel: CustomerViewModel,
    authViewModel: AuthViewModel,
    showMessage: (String) -> Unit,
) {
    composable(Screen.Cart.route) {
        ModernCartScreen(
            viewModel = customerViewModel,
            onNavigateToCheckout = {
                if (currentUser == null) {
                    navController.navigate(Screen.Login.route)
                } else {
                    navController.navigate(Screen.Checkout.route)
                }
            },
            onBackClick = { navController.popBackStack() },
        )
    }

    composable(Screen.Checkout.route) {
        ModernCheckoutScreen(
            viewModel = customerViewModel,
            onOrderSuccess = { orderId ->
                navController.navigate("tracking/$orderId") {
                    popUpTo(Screen.Cart.route) { inclusive = true }
                }
            },
            onBackClick = { navController.popBackStack() },
        )
    }

    composable(Screen.MyOrders.route) {
        MyOrdersScreen(
            onBackClick = { navController.popBackStack() },
            onOrderClick = { orderId -> navController.navigate("tracking/$orderId") },
            viewModel = customerViewModel
        )
    }

    composable(Screen.Profile.route) {
        ProfileScreen(
            onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            onNavigateToOrders = { navController.navigate(Screen.MyOrders.route) },
            onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
            onLogout = {
                authViewModel.logout()
                showMessage("Successfully logged out!")
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            authViewModel = authViewModel
        )
    }

    composable(Screen.EditProfile.route) {
        EditProfileScreen(
            onBackClick = { navController.popBackStack() },
            onSaveSuccess = { navController.popBackStack() },
            authViewModel = authViewModel,
        )
    }

    composable(
        route = "tracking/{orderId}",
        arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
        OrderTrackingScreen(
            orderId = orderId,
            viewModel = customerViewModel,
            onBackClick = { navController.popBackStack() }
        )
    }
}
