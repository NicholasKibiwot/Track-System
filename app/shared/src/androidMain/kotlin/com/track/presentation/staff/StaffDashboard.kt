package com.track.presentation.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboard(
    viewModel: StaffViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val lookupResult by viewModel.lookupResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Staff Dashboard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Order Management System", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1C1E), titleContentColor = Color.White),
                actions = {
                    IconButton(onClick = { /* Profile */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White)
                    }
                }
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
                    value = orders.count { it.orderStatus == OrderStatus.PENDING || it.orderStatus == OrderStatus.PROCESSING }.toString(),
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
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Enter Tracking ID") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.lookupOrder(searchQuery) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1C1E))
                        ) {
                            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            else Text("Search")
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            errorMessage?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(16.dp))
            }

            // Results Section
            lookupResult?.let { order ->
                Text("Lookup Result", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                OrderDetailsCard(
                    order = order,
                    onUpdateStatus = { newStatus -> viewModel.updateOrderStatus(order.id, newStatus) }
                )
            } ?: run {
                if (!isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FindInPage, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                            Spacer(Modifier.height(8.dp))
                            Text("No order selected", color = Color.Gray)
                        }
                    }
                }
            }
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
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = contentColor)
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
            Text("Order Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            
            Text("Customer: ${order.customerName}", fontWeight = FontWeight.Medium)
            Text("Destination: ${order.destination}", color = Color.Gray)
            
            Spacer(Modifier.height(16.dp))
            
            Text("Items (${order.items.size}):", fontWeight = FontWeight.Bold)
            order.items.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
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
