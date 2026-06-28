package com.track.presentation.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.util.kmpViewModel
import com.track.data.FakeData
import com.track.models.Order
import com.track.models.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboard(
    viewModel: DriverViewModel = kmpViewModel(),
    onScanPackage: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val assignedOrders by viewModel.assignedOrders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Driver Panel", fontWeight = FontWeight.Bold)
                        Text("Active Assignments", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1C1E), titleContentColor = Color.White),
                actions = {
                    IconButton(onClick = onScanPackage) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = Color.White)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onScanPackage,
                containerColor = Color(0xFF4C84FF),
                contentColor = Color.White,
                icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
                text = { Text("Pickup Scan") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp),
        ) {
            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DriverStatBox(
                    modifier = Modifier.weight(1f),
                    label = "To Deliver",
                    count = assignedOrders.size.toString(),
                    icon = Icons.Default.LocalShipping,
                    color = Color(0xFF4C84FF)
                )
                DriverStatBox(
                    modifier = Modifier.weight(1f),
                    label = "Completed",
                    count = "0", // Should be from VM
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF2AA952)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Active Deliveries",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1A1C1E))
                    }
                }

                assignedOrders.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Spacer(Modifier.height(12.dp))
                            Text("No assigned deliveries", color = Color.Gray)
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(assignedOrders) { order: Order ->
                            DriverOrderCard(
                                order = order,
                                onUpdateLocation = { viewModel.simulateLocationUpdate(order.id) },
                                onMarkDelivered = { viewModel.updateOrderStatus(order.id, OrderStatus.DELIVERED) },
                                onMarkInTransit = { viewModel.updateOrderStatus(order.id, OrderStatus.INTRANSIT) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverStatBox(
    modifier: Modifier = Modifier,
    label: String,
    count: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(count, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(label, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DriverOrderCard(
    order: Order,
    onUpdateLocation: () -> Unit,
    onMarkDelivered: () -> Unit,
    onMarkInTransit: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFF1F3F4),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "ID: ${order.trackingNumber}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                val statusColor = when (order.orderStatus) {
                    OrderStatus.INTRANSIT -> Color(0xFF4C84FF)
                    OrderStatus.DELIVERED -> Color(0xFF2AA952)
                    OrderStatus.DELAYED -> Color(0xFFEA4335)
                    else -> Color.Gray
                }
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.orderStatus.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Recipient", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEA4335), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(order.destination, fontSize = 14.sp)
                    Text("Delivery Address", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onUpdateLocation,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Location", fontSize = 12.sp)
                }

                if (order.orderStatus != OrderStatus.INTRANSIT && order.orderStatus != OrderStatus.DELIVERED) {
                    Button(
                        onClick = onMarkInTransit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1C1E))
                    ) {
                        Text("Start Trip", fontSize = 12.sp)
                    }
                }

                if (order.orderStatus != OrderStatus.DELIVERED) {
                    Button(
                        onClick = onMarkDelivered,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2AA952))
                    ) {
                        Text("Delivered", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}


