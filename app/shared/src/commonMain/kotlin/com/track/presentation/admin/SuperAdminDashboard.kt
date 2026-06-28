package com.track.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Route model ────────────────────────────────────────────────────────────

enum class SuperAdminSection {
    DASHBOARD, DELIVERIES, LIVE_TRACKING, DISPATCH_QUEUE,
    STAFF, COURIERS, CUSTOMERS, ROLES,
    REGIONS, SERVICE_WINDOWS, PRICING,
    SYSTEM_HEALTH, ERROR_LOGS
}

data class SidebarItem(
    val label: String,
    val icon: ImageVector,
    val section: SuperAdminSection
)

val sidebarGroups = listOf(
    "Operations" to listOf(
        SidebarItem("Dashboard", Icons.Default.Dashboard, SuperAdminSection.DASHBOARD),
        SidebarItem("Deliveries", Icons.Default.LocalShipping, SuperAdminSection.DELIVERIES),
        SidebarItem("Live Tracking", Icons.Default.LocationOn, SuperAdminSection.LIVE_TRACKING),
        SidebarItem("Dispatch Queue", Icons.Default.Queue, SuperAdminSection.DISPATCH_QUEUE),
    ),
    "People" to listOf(
        SidebarItem("Staff", Icons.Default.People, SuperAdminSection.STAFF),
        SidebarItem("Couriers", Icons.Default.DeliveryDining, SuperAdminSection.COURIERS),
        SidebarItem("Customers", Icons.Default.Person, SuperAdminSection.CUSTOMERS),
        SidebarItem("Roles & Permissions", Icons.Default.AdminPanelSettings, SuperAdminSection.ROLES),
    ),
    "Configuration" to listOf(
        SidebarItem("Regions & Routes", Icons.Default.Map, SuperAdminSection.REGIONS),
        SidebarItem("Service Windows", Icons.Default.Schedule, SuperAdminSection.SERVICE_WINDOWS),
        SidebarItem("Pricing & Fees", Icons.Default.AttachMoney, SuperAdminSection.PRICING),
    ),
    "Monitoring" to listOf(
        SidebarItem("System Health", Icons.Default.MonitorHeart, SuperAdminSection.SYSTEM_HEALTH),
        SidebarItem("Error Logs", Icons.Default.BugReport, SuperAdminSection.ERROR_LOGS),
    )
)

// ─── Root layout ─────────────────────────────────────────────────────────────

@Composable
fun SuperAdminDashboard(
    viewModel: SuperAdminViewModel,
    onLogout: () -> Unit = {}
) {
    var currentSection by remember { mutableStateOf(SuperAdminSection.DASHBOARD) }
    var sidebarExpanded by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    val colorScheme = if (darkMode) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = colorScheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left sidebar
                SuperAdminSidebar(
                    expanded = sidebarExpanded,
                    currentSection = currentSection,
                    onSectionSelect = { currentSection = it },
                    onToggleExpand = { sidebarExpanded = !sidebarExpanded }
                )

                // Main content area
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    SuperAdminTopBar(
                        section = currentSection,
                        darkMode = darkMode,
                        onToggleDark = { darkMode = !darkMode },
                        onLogout = onLogout
                    )
                    HorizontalDivider()
                    Box(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                        when (currentSection) {
                            SuperAdminSection.DASHBOARD -> SuperAdminKpiOverview(viewModel)
                            SuperAdminSection.DELIVERIES -> DeliveriesTableScreen(viewModel)
                            SuperAdminSection.DISPATCH_QUEUE -> DispatchQueueScreen(viewModel)
                            SuperAdminSection.STAFF, SuperAdminSection.COURIERS ->
                                StaffRolesScreen(viewModel, currentSection)
                            SuperAdminSection.ROLES -> RolesPermissionsScreen()
                            SuperAdminSection.REGIONS, SuperAdminSection.SERVICE_WINDOWS,
                            SuperAdminSection.PRICING -> RegionsConfigScreen(currentSection)
                            SuperAdminSection.SYSTEM_HEALTH, SuperAdminSection.ERROR_LOGS ->
                                SystemHealthScreen(currentSection)
                            else -> SuperAdminKpiOverview(viewModel)
                        }
                    }
                }
            }
        }
    }
}

// ─── Sidebar ─────────────────────────────────────────────────────────────────

@Composable
fun SuperAdminSidebar(
    expanded: Boolean,
    currentSection: SuperAdminSection,
    onSectionSelect: (SuperAdminSection) -> Unit,
    onToggleExpand: () -> Unit
) {
    val width = if (expanded) 240.dp else 64.dp
    Surface(
        modifier = Modifier.width(width).fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)) {
            // Logo row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (expanded) {
                    Column {
                        Text("Track-System", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Superadmin", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
                IconButton(onClick = onToggleExpand) {
                    Icon(Icons.Default.Menu, contentDescription = "Toggle sidebar")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Groups
            sidebarGroups.forEach { (groupName, items) ->
                if (expanded) {
                    Text(
                        text = groupName.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                items.forEach { item ->
                    val selected = currentSection == item.section
                    val bg = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                    val contentColor = if (selected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg)
                            .clickable { onSectionSelect(item.section) }
                            .padding(horizontal = if (expanded) 12.dp else 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(item.icon, contentDescription = item.label, tint = contentColor, modifier = Modifier.size(20.dp))
                        if (expanded) {
                            Spacer(Modifier.width(12.dp))
                            Text(item.label, fontSize = 13.sp, color = contentColor)
                        }
                    }
                }
                if (expanded) Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.weight(1f))

            // Footer info card
            if (expanded) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Full system control", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Manage roles, routes &\nall deliveries", fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }
    }
}

// ─── Top bar ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminTopBar(
    section: SuperAdminSection,
    darkMode: Boolean,
    onToggleDark: () -> Unit,
    onLogout: () -> Unit
) {
    var statusFilter by remember { mutableStateOf("All") }
    var regionFilter by remember { mutableStateOf("All Regions") }
    var searchQuery by remember { mutableStateOf("") }

    TopAppBar(
        title = {
            Column {
                Text("Global logistics dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Superadmin · ${section.name.lowercase().replace('_', ' ')}",
                    fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        actions = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search delivery, user, ID…", fontSize = 12.sp) },
                modifier = Modifier.width(220.dp).height(48.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(18.dp)) },
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            // Status filter chip
            StatusFilterDropdown(statusFilter) { statusFilter = it }
            Spacer(Modifier.width(8.dp))
            // Region filter chip
            RegionFilterDropdown(regionFilter) { regionFilter = it }
            Spacer(Modifier.width(8.dp))
            // Dark mode
            IconButton(onClick = onToggleDark) {
                Icon(
                    if (darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle dark mode"
                )
            }
            // Notifications badge
            BadgedBox(badge = { Badge { Text("3") } }) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                }
            }
            // Profile
            Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onLogout() },
                contentAlignment = Alignment.Center
            ) {
                Text("NK", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
        }
    )
}

@Composable
fun StatusFilterDropdown(current: String, onSelect: (String) -> Unit) {
    val options = listOf("All", "In transit", "Delivered", "Delayed", "Canceled")
    var expanded by remember { mutableStateOf(false) }
    Box {
        FilterChip(
            selected = current != "All",
            onClick = { expanded = true },
            label = { Text(current, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.FilterList, null, Modifier.size(16.dp)) }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = { onSelect(it); expanded = false })
            }
        }
    }
}

@Composable
fun RegionFilterDropdown(current: String, onSelect: (String) -> Unit) {
    val regions = listOf("All Regions", "Nairobi", "Mombasa", "Eldoret", "Kisumu", "Nakuru")
    var expanded by remember { mutableStateOf(false) }
    Box {
        FilterChip(
            selected = current != "All Regions",
            onClick = { expanded = true },
            label = { Text(current, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Map, null, Modifier.size(16.dp)) }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            regions.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = { onSelect(it); expanded = false })
            }
        }
    }
}
