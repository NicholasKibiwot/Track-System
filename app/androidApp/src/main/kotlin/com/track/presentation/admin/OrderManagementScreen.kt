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
import androidx.hilt.navigation.compose.hiltViewModel
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus

@Composable
fun OrderManagementScreen(viewModel: SuperAdminViewModel = hiltViewModel()) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf("Delivered") }
    val tabs = listOf("Delivered", "Processing", "Cancelled")

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
            OrderList(filteredOrders)
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
private fun OrderList(orders: List<Order>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(orders) { order ->
            AdminOrderCard(order = order)
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
fun AdminOrderCard(order: Order) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order №${order.trackingNumber}", fontWeight = FontWeight.Bold)
                Text("05-12-2023", color = Color.Gray) // Placeholder date
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OrderCardDetails(order)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OrderCardFooter(order)
        }
    }
}

@Composable
private fun OrderCardDetails(order: Order) {
    Row(Modifier.fillMaxWidth()) {
        Text("Tracking number: ", color = Color.Gray)
        Text(order.id, fontWeight = FontWeight.Medium)
    }
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text("Quantity: ", color = Color.Gray)
            Text("${order.items.sumOf { it.quantity }}", fontWeight = FontWeight.Medium)
        }
        Row {
            Text("Total Amount: ", color = Color.Gray)
            Text("KES ${order.totalAmount}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OrderCardFooter(order: Order) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = { /* Details */ },
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Details", color = Color.Black)
        }
        
        Text(
            text = order.orderStatus.name,
            color = getStatusColor(order.orderStatus),
            fontWeight = FontWeight.Medium
        )
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
