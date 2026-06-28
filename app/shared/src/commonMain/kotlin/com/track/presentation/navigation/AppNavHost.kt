package com.track.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.track.models.UserRole
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.customer.CustomerViewModel
import com.track.presentation.admin.SuperAdminViewModel
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

    // Wiring up Customer ID when role is Customer
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (user.role == UserRole.CUSTOMER) {
                customerViewModel.setCustomer(user.id, user.name)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            val role = currentUser?.role

            when {
                currentUser == null || role == null -> {
                    PublicNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        customerViewModel = customerViewModel,
                        showMessage = ::showMessage
                    )
                }
                role == UserRole.CUSTOMER -> {
                    CustomerNavGraph(
                        navController = navController,
                        customerViewModel = customerViewModel,
                        authViewModel = authViewModel,
                        showMessage = ::showMessage
                    )
                }
                role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN -> {
                    AdminNavGraph(
                        navController = navController,
                        adminViewModel = adminViewModel
                    )
                }
                role == UserRole.STAFF -> {
                    StaffNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        showMessage = ::showMessage
                    )
                }
                role == UserRole.DRIVER -> {
                    DriverNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        showMessage = ::showMessage
                    )
                }
            }
        }
    }
}
