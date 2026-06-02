package com.track.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AdminDashboard() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Inventory", "Orders", "Staff")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Super Admin Console") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            Box(Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> InventoryManagementScreen()
                    1 -> OrderManagementScreen()
                    2 -> StaffManagementScreen()
                }
            }
        }
    }
}
