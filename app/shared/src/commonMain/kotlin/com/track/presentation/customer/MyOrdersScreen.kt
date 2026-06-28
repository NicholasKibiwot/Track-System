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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.presentation.customer.CustomerViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    onBackClick: () -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: CustomerViewModel
) {
    val orders by viewModel.myOrders.collectAsState()
    var selectedTab by remember { mutableStateOf("All") }
    val tabs = listOf("All", "Pending Payment", "Delivered", "Processing")

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
            OrderTabRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

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

            OrderList(
                orders = filteredOrders,
                onOrderClick = onOrderClick
            )
        }
    }
}

@Composable
private fun OrderTabRow(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
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
                onClick = { onTabSelected(tab) },
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
}

@Composable
private fun OrderList(
    orders: List<Order>,
    onOrderClick: (String) -> Unit
) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No orders found", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(orders) { order ->
                CustomerOrderCard(order = order, onClick = { onOrderClick(order.id) })
            }
        }
    }
}

@Composable
fun CustomerOrderCard(order: Order, onClick: () -> Unit) {
    val dateString = remember(order.createdAt) {
        try {
            val instant = Instant.fromEpochSeconds(order.createdAt.seconds)
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            "${localDateTime.dayOfMonth}/${localDateTime.monthNumber}/${localDateTime.year}"
        } catch (_: Exception) {
            "Recent"
        }
    }

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
                    text = dateString,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Item Preview (up to 3 items)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    order.items.take(3).forEach { item ->
                        SubcomposeAsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.productName,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF8F9FA)),
                            contentScale = ContentScale.Crop,
                            error = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Search, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
                                }
                            }
                        )
                    }
                    if (order.items.size > 3) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFEEEEEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+${order.items.size - 3}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Row {
                        Text("Quantity: ", color = Color.Gray, fontSize = 12.sp)
                        Text("${order.items.sumOf { it.quantity }}", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    }
                    Row {
                        Text("Total: ", color = Color.Gray, fontSize = 12.sp)
                        Text("KES ${order.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
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

                val statusColor = when (order.orderStatus) {
                    OrderStatus.DELIVERED -> Color(0xFF4CAF50)
                    OrderStatus.CANCELLED -> Color(0xFFF44336)
                    OrderStatus.PENDING -> Color(0xFFFF9800)
                    OrderStatus.SHIPPED, OrderStatus.INTRANSIT -> Color(0xFF2196F3)
                    else -> Color(0xFF9E9E9E)
                }

                Text(
                    text = order.orderStatus.name,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

