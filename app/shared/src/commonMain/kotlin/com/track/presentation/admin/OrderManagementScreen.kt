package com.track.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.util.kmpViewModel
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun OrderManagementScreen(viewModel: SuperAdminViewModel = kmpViewModel()) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf("Processing") }
    val tabs = listOf("Processing", "Delivered", "Cancelled")

    Column(modifier = Modifier.fillMaxSize()) {
        OrderManagementHeader()
        
        OrderManagementTabs(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            LoadingIndicator()
        } else {
            val filteredOrders = filterOrders(orders, selectedTab)
            OrderList(filteredOrders, viewModel)
        }
    }
}

@Composable
private fun OrderManagementHeader() {
    Text(
        "My Orders",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun OrderManagementTabs(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            Button(
                onClick = { onTabSelected(tab) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color.Black else Color.Transparent,
                    contentColor = if (isSelected) Color.White else Color.Black
                ),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(tab)
            }
        }
    }
}

@Composable
private fun OrderList(orders: List<Order>, viewModel: SuperAdminViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(orders) { order ->
            AdminOrderCard(order = order, onUpdateStatus = { newStatus ->
                viewModel.updateOrderStatus(order.id, newStatus)
            })
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

private fun filterOrders(orders: List<Order>, selectedTab: String): List<Order> {
    return orders.filter { 
        when(selectedTab) {
            "Delivered" -> it.orderStatus == OrderStatus.DELIVERED
            "Processing" -> it.orderStatus == OrderStatus.PROCESSING || 
                           it.orderStatus == OrderStatus.PENDING || 
                           it.orderStatus == OrderStatus.SHIPPED || 
                           it.orderStatus == OrderStatus.INTRANSIT
            "Cancelled" -> it.orderStatus == OrderStatus.CANCELLED
            else -> true
        }
    }
}

@Composable
fun AdminOrderCard(order: Order, onUpdateStatus: (OrderStatus) -> Unit) {
    var showStatusMenu by remember { mutableStateOf(false) }
    val dateString = remember(order.createdAt) {
        try {
            val instant = Instant.fromEpochSeconds(order.createdAt.seconds)
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            "${localDateTime.dayOfMonth}/${localDateTime.monthNumber}/${localDateTime.year}"
        } catch (_: Exception) {
            "Recent"
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order №${order.trackingNumber}", fontWeight = FontWeight.Bold)
                Text(dateString, color = Color.Gray, fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OrderCardDetails(order)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    OutlinedButton(
                        onClick = { showStatusMenu = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Update Status", fontSize = 12.sp, color = Color.Black)
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        OrderStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name) },
                                onClick = {
                                    onUpdateStatus(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
                
                Surface(
                    color = getStatusColor(order.orderStatus).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = order.orderStatus.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = getStatusColor(order.orderStatus),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderCardDetails(order: Order) {
    Row(Modifier.fillMaxWidth()) {
        Text("Customer: ", color = Color.Gray, fontSize = 13.sp)
        Text(order.customerName, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
    
    Spacer(Modifier.height(4.dp))

    Row(Modifier.fillMaxWidth()) {
        Text("Destination: ", color = Color.Gray, fontSize = 13.sp)
        Text(order.destination, fontWeight = FontWeight.Medium, fontSize = 13.sp, maxLines = 1)
    }

    Spacer(Modifier.height(8.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text("Items: ", color = Color.Gray, fontSize = 13.sp)
            Text("${order.items.sumOf { it.quantity }}", fontWeight = FontWeight.Medium, fontSize = 13.sp)
        }
        Row {
            Text("Total: ", color = Color.Gray, fontSize = 13.sp)
            Text("KES ${order.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFB12704))
        }
    }
}

private fun getStatusColor(status: OrderStatus): Color {
    return when(status) {
        OrderStatus.DELIVERED -> Color(0xFF2AA952)
        OrderStatus.CANCELLED -> Color(0xFFF01F0E)
        else -> Color(0xFFF5A623)
    }
}

@Preview(showBackground = true)
@Composable
fun OrderManagementScreenPreview() {
    OrderManagementScreen()
}

