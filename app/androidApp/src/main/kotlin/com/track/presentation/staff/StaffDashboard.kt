package com.track.presentation.staff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun StaffDashboard(
    viewModel: StaffViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val lookupResult by viewModel.lookupResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Desk - Order Tracking") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Search Section
            Text("Enter Item ID / Tracking Number", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("e.g. TRK-9921") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.lookupOrder(searchQuery) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Icon(Icons.Default.Search, contentDescription = null)
                }
            }

            Spacer(Modifier.height(24.dp))

            errorMessage?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(16.dp))
            }

            // Results Section
            lookupResult?.let { order ->
                OrderDetailsCard(
                    order = order,
                    onUpdateStatus = { newStatus -> viewModel.updateOrderStatus(order.id, newStatus) }
                )
            } ?: run {
                if (!isLoading) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("No order selected. Enter an ID to begin.", color = Color.Gray)
                    }
                }
            }
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
