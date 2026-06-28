package com.track.presentation.admin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.models.OrderStatus
import com.track.models.PaymentStatus

@Composable
fun DeliveriesTableScreen(viewModel: SuperAdminViewModel) {
    val orders by viewModel.orders.collectAsState()
    var statusFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = orders.filter { order ->
        val matchStatus = statusFilter == "All" || order.orderStatus.name.contains(statusFilter, ignoreCase = true)
        val matchSearch = searchQuery.isEmpty() ||
            order.id.contains(searchQuery, ignoreCase = true) ||
            order.customerName.contains(searchQuery, ignoreCase = true)
        matchStatus && matchSearch
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // Header bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("All Deliveries", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search…", fontSize = 12.sp) },
                    modifier = Modifier.width(200.dp).height(44.dp),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(16.dp)) },
                    shape = RoundedCornerShape(22.dp)
                )
                StatusFilterDropdown(statusFilter) { statusFilter = it }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(0.dp)) {
                // Table header
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    listOf("Tracking ID" to 1.5f, "Customer" to 1f, "Courier" to 1f,
                        "Pickup" to 1f, "Drop-off" to 1f, "Status" to 1f,
                        "Payment" to 0.8f, "ETA" to 0.8f, "Actions" to 1f
                    ).forEach { (col, weight) ->
                        Text(col, modifier = Modifier.weight(weight), fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                HorizontalDivider()

                if (filtered.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Text("No deliveries match the current filters.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 600.dp)) {
                        items(filtered) { order ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(order.id.take(12), modifier = Modifier.weight(1.5f), fontSize = 12.sp)
                                Text(order.customerName.take(10), modifier = Modifier.weight(1f), fontSize = 12.sp)
                                Text((order.driverName ?: "–").take(10), modifier = Modifier.weight(1f), fontSize = 12.sp)
                                Text(order.origin.take(10), modifier = Modifier.weight(1f), fontSize = 12.sp)
                                Text(order.destination.take(10), modifier = Modifier.weight(1f), fontSize = 12.sp)
                                Box(modifier = Modifier.weight(1f)) { StatusBadge(order.orderStatus.name) }
                                Box(modifier = Modifier.weight(0.8f)) {
                                    PaymentBadge(if (order.paymentStatus == PaymentStatus.PAID) "Paid" else "Pending")
                                }
                                Text("–", modifier = Modifier.weight(0.8f), fontSize = 11.sp)
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {},
                                        modifier = Modifier.size(28.dp)
                                    ) { Icon(Icons.Default.Visibility, null, Modifier.size(14.dp)) }
                                    IconButton(
                                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.INTRANSIT) },
                                        modifier = Modifier.size(28.dp)
                                    ) { Icon(Icons.Default.AltRoute, null, Modifier.size(14.dp)) }
                                    IconButton(
                                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.CANCELLED) },
                                        modifier = Modifier.size(28.dp)
                                    ) { Icon(Icons.Default.Cancel, null, Modifier.size(14.dp)) }
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusFilterDropdown(selectedStatus: String, onStatusSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf("All", "Pending", "Processing", "InTransit", "Delivered", "Cancelled")

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.height(44.dp)
        ) {
            Text(selectedStatus, fontSize = 12.sp)
            Icon(Icons.Default.ArrowDropDown, null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            statuses.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}
