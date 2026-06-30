package com.track.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.track.models.Order
import com.track.models.OrderStatus
import com.track.presentation.customer.CustomerViewModel
import com.track.util.kmpViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopOrdersScreen(
    onBackClick: () -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: CustomerViewModel = kmpViewModel<CustomerViewModel>()
) {
    val orders by viewModel.myOrders.collectAsState()
    val activeOrders = orders.filter { it.orderStatus != OrderStatus.DELIVERED && it.orderStatus != OrderStatus.CANCELLED && it.orderStatus != OrderStatus.RETURNED }
    val completedOrders = orders.filter { it.orderStatus == OrderStatus.DELIVERED }
    val cartItems by viewModel.cartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = ShopPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("T", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Track Shop", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Creative Commerce", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, null, tint = Color.Gray) }
                    IconButton(onClick = {}) { Icon(Icons.Default.WbSunny, null, tint = Color.Gray) }
                    IconButton(onClick = {}) { Icon(Icons.Default.NotificationsNone, null, tint = Color.Gray) }
                }
            )
        },
        bottomBar = {
            ShopBottomNav(
                selectedTab = 3,
                onTabSelected = { 
                    when(it) {
                        0 -> onBackClick()
                        1 -> {}
                        2 -> { /* Navigate to cart */ }
                        3 -> {}
                        4 -> { /* Navigate to profile */ }
                    }
                },
                cartCount = cartCount
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ShopBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("My Orders,", color = ShopPrimary, fontSize = 14.sp)
                Text("${activeOrders.size} Order active", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF311B92))
            }

            items(activeOrders) { order ->
                ActiveOrderCard(order, onOrderClick)
            }

            if (completedOrders.isNotEmpty()) {
                item {
                    Text("Older Orders (${completedOrders.size})", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }

                items(completedOrders) { order ->
                    CompletedOrderCard(order, onOrderClick)
                }
            }
        }
    }
}

@Composable
fun ActiveOrderCard(order: Order, onClick: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        onClick = { onClick(order.id) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        order.orderStatus.name.lowercase().replaceFirstChar { it.uppercaseChar() },
                        color = Color(0xFF2E7D32),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Icon(Icons.Default.ArrowForward, null, tint = ShopPrimary)
            }
            
            Spacer(Modifier.height(16.dp))
            
            OrderStatusTimeline(order.orderStatus)
            
            Spacer(Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("$ ${order.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("#${order.trackingNumber.ifBlank { order.id.takeLast(8) }}", color = Color.Gray, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(formatDate(order.createdAt.seconds), fontSize = 14.sp)
                    Text("${order.items.size} items", color = Color.Gray, fontSize = 12.sp)
                }
                
                Row {
                    order.items.take(2).forEach { item ->
                        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.LightGray) { }
                        Spacer(Modifier.width(4.dp))
                    }
                    if (order.items.size > 2) {
                        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color(0xFF311B92)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("+${order.items.size - 2}", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusTimeline(currentStatus: OrderStatus) {
    val statuses = listOf("Ordered", "Accepted", "Ready", "Shipped", "Delivered")
    val currentIndex = when (currentStatus) {
        OrderStatus.PENDING -> 0
        OrderStatus.ACCEPTED -> 1
        OrderStatus.PROCESSING -> 2
        OrderStatus.SHIPPED -> 3
        OrderStatus.INTRANSIT -> 3
        OrderStatus.DELIVERED -> 4
        else -> 0
    }
    
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            statuses.forEachIndexed { index, status ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    if (index < statuses.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(if (index < currentIndex) ShopPrimary else Color.LightGray)
                        )
                    }
                    Surface(
                        modifier = Modifier.size(12.dp),
                        shape = CircleShape,
                        color = if (index <= currentIndex) ShopPrimary else Color.LightGray
                    ) {}
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            statuses.forEachIndexed { index, status ->
                Text(
                    status,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    color = if (index <= currentIndex) Color.Black else Color.Gray
                )
            }
        }
    }
}

@Composable
fun CompletedOrderCard(order: Order, onClick: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        onClick = { onClick(order.id) }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Completed", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text("$ ${order.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("#${order.trackingNumber.ifBlank { order.id.takeLast(8) }}", color = Color.Gray, fontSize = 11.sp)
            }
            
            Column(modifier = Modifier.weight(1.5f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(formatDate(order.createdAt.seconds), fontSize = 12.sp)
                Text("${order.items.size} items", color = Color.Gray, fontSize = 11.sp)
            }
            
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                order.items.take(2).forEach { _ ->
                    Surface(modifier = Modifier.size(30.dp), shape = CircleShape, color = Color.LightGray) { }
                    Spacer(Modifier.width(2.dp))
                }
                Icon(Icons.Default.ArrowForward, null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
            }
        }
    }
}

fun formatDate(seconds: Long): String {
    return try {
        val instant = Instant.fromEpochSeconds(seconds)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.dayOfMonth} ${localDateTime.month.name.lowercase().take(3).replaceFirstChar { it.uppercase() }} ${localDateTime.year}"
    } catch (_: Exception) {
        "Date"
    }
}
