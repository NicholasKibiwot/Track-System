package com.track.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.track.presentation.customer.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    onBackClick: () -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: CustomerViewModel
) {
    val orders by viewModel.myOrders.collectAsState()
    var selectedTab by remember { mutableStateOf("Delivered") }
    val tabs = listOf("Pending Payment", "Delivered", "Processing")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOf(selectedTab),
                containerColor = Color.White,
                contentColor = Color(0xFF4C84FF),
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab,
                                color = if (selectedTab == tab) Color(0xFF4C84FF) else Color.Gray,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            val filteredOrders = remember(orders, selectedTab) {
                orders.filter { order ->
                    when (selectedTab) {
                        "Pending Payment" -> order.paymentStatus == "PENDING"
                        "Delivered" -> order.orderStatus == OrderStatus.DELIVERED
                        "Processing" -> order.orderStatus == OrderStatus.PROCESSING || order.orderStatus == OrderStatus.PENDING
                        else -> true
                    }
                }
            }

            if (filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No orders found", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredOrders) { order ->
                        CustomerOrderCard(order = order, onClick = { onOrderClick(order.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerOrderCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order No: ${order.trackingNumber.ifBlank { order.id.takeLast(8) }}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "12-5-2022", // Placeholder date, should use order.createdAt
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                Text("Tracking number: ", color = Color.Gray, fontSize = 12.sp)
                Text(order.trackingNumber, fontWeight = FontWeight.Medium, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text("Quantity: ", color = Color.Gray, fontSize = 12.sp)
                    Text("${order.items.sumOf { it.quantity }}", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                }
                Row {
                    Text("Total Amount: ", color = Color.Gray, fontSize = 12.sp)
                    Text("${order.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Details", color = Color.Black, fontSize = 12.sp)
                }
                Text(
                    text = order.orderStatus.name,
                    color = if (order.orderStatus == OrderStatus.DELIVERED) Color(0xFF4CAF50) else Color(0xFFFF9800),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

