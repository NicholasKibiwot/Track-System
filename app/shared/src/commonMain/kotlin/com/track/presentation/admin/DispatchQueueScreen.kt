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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.domain.models.OrderStatus

@Composable
fun DispatchQueueScreen(viewModel: SuperAdminViewModel) {
    val orders by viewModel.orders.collectAsState()
    val staff by viewModel.staffUsers.collectAsState()
    var autoAssign by remember { mutableStateOf(false) }

    val pendingOrders = orders.filter {
        it.orderStatus == OrderStatus.PENDING
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dispatch Queue", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Auto-assign", fontSize = 13.sp)
                Spacer(Modifier.width(8.dp))
                Switch(checked = autoAssign, onCheckedChange = { autoAssign = it })
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "${pendingOrders.size} orders waiting assignment",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(16.dp))

        if (pendingOrders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Dispatch queue is clear!", fontWeight = FontWeight.SemiBold)
                        Text("All orders have been assigned.", color = Color(0xFF388E3C), fontSize = 13.sp)
                    }
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(pendingOrders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("#${order.id.take(10)}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Customer: ${order.userId.take(16)}", fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("To: ${order.deliveryAddress?.take(24) ?: "Unknown"}",
                                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            // Priority badge
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFF9800).copy(alpha = 0.15f)
                            ) {
                                Text("Normal", color = Color(0xFFE65100), fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            // Assign dropdown
                            var assignMenuOpen by remember { mutableStateOf(false) }
                            Box {
                                Button(
                                    onClick = { assignMenuOpen = true },
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("Assign courier", fontSize = 12.sp)
                                    Icon(Icons.Default.ArrowDropDown, null, Modifier.size(16.dp))
                                }
                                DropdownMenu(expanded = assignMenuOpen, onDismissRequest = { assignMenuOpen = false }) {
                                    if (staff.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No couriers available") },
                                            onClick = { assignMenuOpen = false }
                                        )
                                    } else {
                                        staff.take(8).forEach { s ->
                                            DropdownMenuItem(
                                                text = { Text(s.name) },
                                                onClick = {
                                                    // Could call viewModel.assignCourier(order.id, s.id)
                                                    assignMenuOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
