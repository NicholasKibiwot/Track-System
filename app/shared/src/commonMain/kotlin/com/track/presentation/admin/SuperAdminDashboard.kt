package com.track.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SuperAdminDashboard(
    viewModel: SuperAdminViewModel,
    onAddProductClick: () -> Unit = {},
    onAddStaffClick: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Orders & Payments", "Staff Management", "Inventory")

    Scaffold(topBar = { TopAppBar(title = { Text("Super Admin") }) }) { padding ->
        Column(Modifier.padding(padding)) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }
            Box(Modifier.fillMaxSize().padding(16.dp)) {
                when (selectedTab) {
                    0 -> OrderManagementScreen(viewModel = viewModel)
                    1 -> StaffManagementScreen(viewModel = viewModel, onAddUserClick = onAddStaffClick)
                    2 -> InventoryManagementScreen(viewModel = viewModel, onAddProductClick = onAddProductClick)
                }
            }
        }
    }
}
