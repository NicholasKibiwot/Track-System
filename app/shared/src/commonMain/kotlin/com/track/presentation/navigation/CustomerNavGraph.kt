package com.track.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.customer.*

@Composable
fun CustomerNavGraph(
    navController: NavHostController,
    customerViewModel: CustomerViewModel,
    authViewModel: AuthViewModel,
    showMessage: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            ShopHomeScreen(
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToProductDetails = { productId -> navController.navigate("product_details/$productId") },
                onNavigateToCategory = { categoryId -> navController.navigate("category_products/$categoryId") },
                onNavigateToOrders = { navController.navigate(Screen.MyOrders.route) },
                viewModel = customerViewModel
            )
        }

        composable(Screen.Cart.route) {
            ShopCartScreen(
                viewModel = customerViewModel,
                onNavigateToCheckout = { navController.navigate(Screen.Checkout.route) },
                onBackClick = { navController.popBackStack() }
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
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.MyOrders.route) {
            ShopOrdersScreen(
                onBackClick = { navController.popBackStack() },
                onOrderClick = { orderId -> navController.navigate("tracking/$orderId") },
                viewModel = customerViewModel
            )
        }

        composable(
            route = "tracking/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            OrderTrackingScreen(
                orderId = orderId,
                viewModel = customerViewModel,
                onBackClick = { navController.popBackStack() }
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
                    showMessage("Logged out successfully")
                },
                authViewModel = authViewModel
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }

        composable(
            route = "product_details/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            ProductDetailsScreen(
                productId = productId,
                viewModel = customerViewModel,
                onBackClick = { navController.popBackStack() },
                onAddToCart = { product -> customerViewModel.addToCart(product) }
            )
        }

        composable(
            route = "category_products/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            CategoryProductsScreen(
                categoryId = categoryId,
                viewModel = customerViewModel,
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId -> navController.navigate("product_details/$productId") }
            )
        }
    }
}
