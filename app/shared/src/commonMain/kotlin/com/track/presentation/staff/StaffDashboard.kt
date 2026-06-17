package com.track.presentation.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.domain.models.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboard(
    viewModel: StaffViewModel,
    onLogout: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val lookupResult by viewModel.lookupResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val products by viewModel.products.collectAsState()

    var showInventory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StaffDashboardTopBar(
                showInventory = showInventory,
                onToggleInventory = { showInventory = !showInventory },
                onLogout = onLogout
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (showInventory) {
                InventorySection(products = products)
            } else {
                MainStaffSection(
                    orders = orders,
                    searchState = SearchState(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { viewModel.lookupOrder(searchQuery) },
                        isLoading = isLoading,
                        errorMessage = errorMessage
                    ),
                    lookupResult = lookupResult,
                    onUpdateOrderStatus = { orderId, newStatus ->
                        viewModel.updateOrderStatus(orderId, newStatus)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StaffDashboardTopBar(
    showInventory: Boolean,
    onToggleInventory: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    "Staff Dashboard",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Order Management System",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1A1C1E),
            titleContentColor = Color.White
        ),
        actions = {
            IconButton(onClick = onToggleInventory) {
                Icon(
                    if (showInventory) Icons.AutoMirrored.Filled.Assignment else Icons.Default.Inventory,
                    contentDescription = "Inventory",
                    tint = Color.White
                )
            }
            IconButton(onClick = onLogout) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
private fun InventorySection(products: List<Product>) {
    Text(
        "All Branch Inventory",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(12.dp))
    products.forEach { product ->
        InventoryItemCard(product)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun MainStaffSection(
    orders: List<Order>,
    searchState: SearchState,
    lookupResult: Order?,
    onUpdateOrderStatus: (String, OrderStatus) -> Unit
) {
    // Stats Overview
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Total",
            value = orders.size.toString(),
            containerColor = Color.White,
            contentColor = Color.Black
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Pending",
            value = orders.count {
                it.orderStatus == OrderStatus.PENDING || it.orderStatus == OrderStatus.PROCESSING
            }.toString(),
            containerColor = Color(0xFFFFF4E5),
            contentColor = Color(0xFFFF9800)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Delivered",
            value = orders.count { it.orderStatus == OrderStatus.DELIVERED }.toString(),
            containerColor = Color(0xFFE8F5E9),
            contentColor = Color(0xFF4CAF50)
        )
    }

    Spacer(Modifier.height(24.dp))

    // Search Section
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Search & Lookup", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchState.query,
                    onValueChange = searchState.onQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Enter Tracking ID") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = searchState.onSearch,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1C1E))
                ) {
                    if (searchState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Search")
                    }
                }
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    searchState.errorMessage?.let {
        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
    }

    // Results Section
    if (lookupResult != null) {
        Text(
            "Lookup Result",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        OrderDetailsCard(
            order = lookupResult,
            onUpdateStatus = { newStatus -> onUpdateOrderStatus(lookupResult.id, newStatus) }
        )
    } else if (!searchState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.FindInPage,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.LightGray
                )
                Spacer(Modifier.height(8.dp))
                Text("No order selected", color = Color.Gray)
            }
        }
    }

    Spacer(Modifier.height(32.dp))

    // Notification Area (Simulated)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE3F2FD),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2196F3))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1976D2))
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Customer Inquiry", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                Text(
                    "You have a new inquiry about order TRK-9921. Even when offline, you\u0027ll receive these via email.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

data class SearchState(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val onSearch: () -> Unit,
    val isLoading: Boolean,
    val errorMessage: String?
)

@Composable
fun InventoryItemCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Store,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        product.branch.ifBlank { "Main Store" },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            Text(
                "Stock: ${product.stock}",
                color = if (product.stock < 5) Color.Red else Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 12.sp, color = contentColor.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor
            )
        }
    }
}

@Composable
fun OrderDetailsCard(order: Order, onUpdateStatus: (OrderStatus) -> Unit) {
    var showScanSim by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Order Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            Text("Customer: ${order.customerName}", fontWeight = FontWeight.Medium)
            Text("Destination: ${order.destination}", color = Color.Gray)

            Spacer(Modifier.height(16.dp))

            Text("Items (${order.items.size}):", fontWeight = FontWeight.Bold)
            order.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("- ${item.productName}", modifier = Modifier.weight(1f))
                    Text("x${item.quantity}")
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Current Status: ", fontWeight = FontWeight.Medium)
                Text(
                    order.orderStatus.name,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onUpdateStatus(OrderStatus.DELIVERED) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2AA952))
                ) {
                    Text("MARK DELIVERED")
                }

                OutlinedButton(
                    onClick = { showScanSim = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("SCAN")
                }
            }
        }
    }

    if (showScanSim) {
        AlertDialog(
            onDismissRequest = { showScanSim = false },
            title = { Text("Simulate Package Scan") },
            text = { Text("This would open the camera to scan the package barcode for this order.") },
            confirmButton = { Button(onClick = { showScanSim = false }) { Text("OK") } }
        )
    }
}

