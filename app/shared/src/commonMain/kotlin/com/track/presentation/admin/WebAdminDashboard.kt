package com.track.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.util.kmpViewModel

@Composable
fun WebAdminDashboard(
    viewModel: SuperAdminViewModel = kmpViewModel<SuperAdminViewModel>(),
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val menuItems = listOf(
        AdminMenuItem("Inventory", Icons.Default.Inventory),
        AdminMenuItem("Orders", Icons.AutoMirrored.Filled.ListAlt),
        AdminMenuItem("Staff Management", Icons.Default.People),
        AdminMenuItem("Fleet Tracking", Icons.Default.Map),
        AdminMenuItem("Analytics", Icons.Default.BarChart),
        AdminMenuItem("Settings", Icons.Default.Settings)
    )

    Row(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5))) {
        // Side Navigation Drawer
        Surface(
            modifier = Modifier.width(280.dp).fillMaxHeight(),
            color = Color(0xFF1A1C1E),
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Brand Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 24.dp, horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color(0xFF4C84FF), modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "TRACK ADMIN",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Menu Items
                menuItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    NavigationMenuItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = { selectedTab = index }
                    )
                }

                Spacer(Modifier.weight(1f))

                // Logout Button at bottom
                NavigationMenuItem(
                    item = AdminMenuItem("Logout", Icons.AutoMirrored.Filled.Logout),
                    isSelected = false,
                    onClick = onLogout,
                    contentColor = Color(0xFFFF4D4D)
                )
            }
        }

        // Main Content Area
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth().height(70.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = menuItems[selectedTab].title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // User Profile Mini
                        Column(horizontalAlignment = Alignment.End) {
                            Text("System Administrator", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text("admin@track.com", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Spacer(Modifier.width(12.dp))
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF4C84FF)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("A", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Dashboard Content
            Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                when (selectedTab) {
                    0 -> InventoryManagementScreen(viewModel = viewModel)
                    1 -> OrderManagementScreen(viewModel = viewModel)
                    2 -> StaffManagementScreen(viewModel = viewModel)
                    3 -> FleetTrackingPlaceholder()
                    4 -> AnalyticsPlaceholder()
                    5 -> SettingsPlaceholder()
                }
            }
        }
    }
}

@Composable
fun FleetTrackingPlaceholder() {
    Card(modifier = Modifier.fillMaxSize(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Box(Modifier.fillMaxSize()) {
            // In a real app, integrate Google Maps here
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                Spacer(Modifier.height(16.dp))
                Text("Live Fleet Tracking Map", style = MaterialTheme.typography.titleLarge, color = Color.Gray)
                Text("Real-time GPS data for all active drivers", color = Color.LightGray)
            }
            
            // Sidebar in map
            Surface(
                modifier = Modifier.align(Alignment.CenterStart).padding(16.dp).width(200.dp),
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Active Drivers", fontWeight = FontWeight.Bold)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("• Driver John (TRK-01)", fontSize = 12.sp)
                    Text("• Driver Sarah (TRK-05)", fontSize = 12.sp)
                    Text("• Driver Mike (TRK-12)", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun NavigationMenuItem(
    item: AdminMenuItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    contentColor: Color = if (isSelected) Color.White else Color(0xFF9499A1)
) {
    val backgroundColor = if (isSelected) Color(0xFF4C84FF).copy(alpha = 0.15f) else Color.Transparent
    
    Surface(
        onClick = onClick,
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF4C84FF) else contentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = item.title,
                color = if (isSelected) Color.White else contentColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun AnalyticsPlaceholder() {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            StatCard("Total Revenue", "KES 1.2M", "+12%", Modifier.weight(1f))
            StatCard("Active Orders", "154", "+5%", Modifier.weight(1f))
            StatCard("Fleet Status", "92% Active", "-2%", Modifier.weight(1f))
        }
        
        Card(modifier = Modifier.fillMaxWidth().weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Charts and Graphs will be displayed here", color = Color.Gray)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, change: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(24.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text(change, color = if (change.startsWith("+")) Color(0xFF4CAF50) else Color(0xFFF44336), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SettingsPlaceholder() {
    Card(modifier = Modifier.fillMaxSize(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("General Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            HorizontalDivider()
            // Just some placeholders
            SettingRow("Company Name", "Track Logistics Ltd")
            SettingRow("System Currency", "KES")
            SettingRow("Notification Emails", "alerts@track.com")
            
            Spacer(Modifier.height(24.dp))
            Button(onClick = {}) { Text("Save Changes") }
        }
    }
}

@Composable
fun SettingRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value, color = Color.Gray)
    }
}
