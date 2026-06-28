package com.track.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.util.kmpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    viewModel: SuperAdminViewModel = kmpViewModel(),
    onLogout: () -> Unit = {},
    onAddProductClick: () -> Unit = {},
    onAddStaffClick: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(0) }
    val menuItems = listOf(
        AdminMenuItem("Inventory", Icons.Default.Inventory),
        AdminMenuItem("Orders", Icons.AutoMirrored.Filled.ListAlt),
        AdminMenuItem("Staff", Icons.Default.People)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("Admin Control Panel", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1C1E), titleContentColor = Color.White),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                menuItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1A1C1E),
                            selectedTextColor = Color(0xFF1A1C1E),
                            indicatorColor = Color(0xFF4C84FF).copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            when (selectedTab) {
                0 -> InventoryManagementScreen(viewModel = viewModel, onAddProductClick = onAddProductClick)
                1 -> OrderManagementScreen(viewModel = viewModel)
                2 -> StaffManagementScreen(viewModel = viewModel, onAddUserClick = onAddStaffClick)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    MaterialTheme {
        AdminDashboard()
    }
}

data class AdminMenuItem(val title: String, val icon: ImageVector)


