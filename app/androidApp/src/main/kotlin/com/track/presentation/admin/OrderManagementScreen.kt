package com.track.presentation.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.track.data.FakeData
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.presentation.admin.SuperAdminViewModel

@Composable
fun OrderManagementScreen(viewModel: SuperAdminViewModel = hiltViewModel()) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        orders.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No orders yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        else -> {
            OrderManagementContent(
                orders = orders,
                onStatusChange = { order, newStatus ->
                    viewModel.updateOrderStatus(order.id, newStatus)
                },
            )
        }
    }
}

@Composable
fun OrderManagementContent(
    orders: List<Order>,
    onStatusChange: (Order, OrderStatus) -> Unit,
) {
    var selectedOrder by remember { mutableStateOf<Order?>(null) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        items(orders) { order: Order ->
            Card(
                onClick = { selectedOrder = order },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "ID: ${order.id}",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            order.orderStatus.name,
                            style = MaterialTheme.typography.labelMedium,
                            color =
                                when (order.orderStatus) {
                                    OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primary
                                    OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
                                    OrderStatus.INTRANSIT -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Customer: ${order.customerName}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        "Total: KES ${order.totalAmount}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        "Payment: ${order.paymentMethod} • ${order.paymentStatus}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    // Order detail / manage dialog
    selectedOrder?.let { order ->
        AlertDialog(
            onDismissRequest = { selectedOrder = null },
            title = {
                Text("Manage Order")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Tracking: ${order.trackingNumber}",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text("Customer: ${order.customerName}")
                    Text("Origin: ${order.origin}")
                    Text("Destination: ${order.destination}")
                    Text(
                        "Driver: ${order.driverName ?: "Not assigned"}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Divider()
                    Text(
                        "Items",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    order.items.forEach { item ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("${item.quantity}x ${item.productName}")
                            Text("KES ${item.unitPrice * item.quantity}")
                        }
                    }
                    Divider()
                    Text(
                        "Total: KES ${order.totalAmount}",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Update Status",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    // Status chips — only show valid next statuses
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            OrderStatus.CONFIRMED,
                            OrderStatus.PROCESSING,
                            OrderStatus.SHIPPED,
                            OrderStatus.INTRANSIT,
                            OrderStatus.DELIVERED,
                            OrderStatus.CANCELLED,
                        ).forEach { status ->
                            FilterChip(
                                selected = order.orderStatus == status,
                                onClick = {
                                    onStatusChange(order, status)
                                    selectedOrder = order.copy(orderStatus = status)
                                },
                                label = {
                                    Text(
                                        status.name,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                },
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedOrder = null }) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedOrder = null }) {
                    Text("Close")
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrderManagementScreenPreview() {
    OrderManagementContent(
        orders = FakeData.previewOrders,
        onStatusChange = { _, _ -> },
    )
}
