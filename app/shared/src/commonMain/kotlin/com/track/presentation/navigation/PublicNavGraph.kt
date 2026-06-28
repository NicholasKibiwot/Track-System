package com.track.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.auth.RegisterScreen
import com.track.presentation.customer.CategoryProductsScreen
import com.track.presentation.customer.CustomerLoginContainer
import com.track.presentation.customer.CustomerProfileCompletionScreen
import com.track.presentation.customer.CustomerViewModel
import com.track.presentation.customer.ProductDetailsScreen
import com.track.presentation.home.ModernHomeScreen
import com.track.presentation.welcome.WelcomeScreen

@Composable
fun PublicNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    customerViewModel: CustomerViewModel,
    showMessage: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Screen.Home.route) },
                onSignIn = { navController.navigate(Screen.Login.route) },
            )
        }

        composable(Screen.Home.route) {
            ModernHomeScreen(
                onNavigateToCart = { navController.navigate(Screen.Login.route) },
                onNavigateToProfile = { navController.navigate(Screen.Login.route) },
                onNavigateToProductDetails = { productId -> navController.navigate("product_details/$productId") },
                onNavigateToCategory = { categoryId -> navController.navigate("category_products/$categoryId") },
                viewModel = customerViewModel
            )
        }

        composable(Screen.Login.route) {
            CustomerLoginContainer(
                onProfileCompleted = {
                    showMessage("Successfully logged in!")
                },
                onForgotPasswordClick = { navController.navigate("forgot_password") },
                onBackClick = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
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
                onAddToCart = { _ -> navController.navigate(Screen.Login.route) }
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
