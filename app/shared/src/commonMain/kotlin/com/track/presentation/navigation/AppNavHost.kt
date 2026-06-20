package com.track.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.track.presentation.auth.ForgotPasswordScreen
import com.track.presentation.auth.LoginScreen
import com.track.presentation.auth.RegisterScreen
import com.track.presentation.customer.*
import com.track.presentation.customer.ProductDetailsScreen
import com.track.presentation.home.HomeScreen
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.customer.CustomerViewModel
import com.track.presentation.admin.SuperAdminViewModel
import com.track.presentation.welcome.WelcomeScreen
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
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(padding)
        ) {
            addPublicRoutes(navController, customerViewModel, authViewModel)
            addAuthRoutes(navController, authViewModel, ::showMessage)
            addCustomerRoutes(navController, currentUser, customerViewModel, authViewModel, ::showMessage)
            addAdminRoutes(navController, adminViewModel)
        }
    }
}

private fun NavGraphBuilder.addAdminRoutes(
    navController: NavHostController,
    adminViewModel: SuperAdminViewModel,
) {
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
        HomeScreen(
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
        CartScreen(
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
        CheckoutScreen(
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
        val order = customerViewModel.getOrderById(orderId)
        val trackingSuccess by customerViewModel.orderSuccess.collectAsState()

        Box(
            Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (trackingSuccess != null) {
                    Text(
                        "SUCCESS!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        "Tracking ID: $trackingSuccess",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Button(onClick = { customerViewModel.clearOrderSuccess() }) {
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

