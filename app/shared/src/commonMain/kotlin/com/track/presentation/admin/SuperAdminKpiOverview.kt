package com.track.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.models.PaymentStatus

// ─── KPI overview (main dashboard screen) ────────────────────────────────────

@Composable
fun SuperAdminKpiOverview(viewModel: SuperAdminViewModel) {
    val orders by viewModel.orders.collectAsState()
    val staffUsers by viewModel.staffUsers.collectAsState()
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // Loading / error banners
        if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        errorMessage?.let {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 13.sp)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { viewModel.clearError() }) { Text("Dismiss") }
                }
            }
        }

        // KPI strip
        Text("Overview", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                modifier = Modifier.weight(1f),
                label = "Total deliveries",
                value = viewModel.totalOrders.toString(),
                delta = "+12 vs yesterday",
                icon = Icons.Default.LocalShipping,
                iconTint = MaterialTheme.colorScheme.primary
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                label = "Delivered today",
                value = viewModel.deliveredOrders.toString(),
                delta = "+5 vs yesterday",
                icon = Icons.Default.CheckCircle,
                iconTint = Color(0xFF4CAF50)
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                label = "In transit",
                value = viewModel.inTransitOrders.toString(),
                delta = "",
                icon = Icons.Default.DirectionsCar,
                iconTint = Color(0xFF2196F3)
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                label = "Pending orders",
                value = viewModel.pendingOrders.toString(),
                delta = "",
                icon = Icons.Default.Pending,
                iconTint = Color(0xFFFF9800)
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                label = "Active couriers",
                value = viewModel.activeStaff.toString(),
                delta = "",
                icon = Icons.Default.People,
                iconTint = Color(0xFF9C27B0)
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                label = "Low stock items",
                value = viewModel.lowStockCount.toString(),
                delta = "",
                icon = Icons.Default.Warning,
                iconTint = Color(0xFFF44336)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Two-column split: deliveries table left, queue+alerts right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left: recent deliveries
            Card(
                modifier = Modifier.weight(2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recent deliveries", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(12.dp))
                    DeliveryTableHeader()
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    if (orders.isEmpty() && !isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No deliveries yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        orders.take(8).forEach { order ->
                            DeliveryRow(
                                trackingId = order.id,
                                customer = order.customerName,
                                status = order.orderStatus.name,
                                paymentStatus = if (order.paymentStatus == PaymentStatus.PAID) "Paid" else "Pending",
                                courier = order.driverName ?: "–"
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }

            // Right column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dispatch queue summary
                Card(shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Dispatch queue", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.height(8.dp))
                        val pending = orders.filter { it.orderStatus.name == "PENDING" }.take(4)
                        if (pending.isEmpty()) {
                            Text("Queue is clear ✓", color = Color(0xFF4CAF50), fontSize = 13.sp)
                        } else {
                            pending.forEach { order ->
                                DispatchQueueRow(orderId = order.id, priority = "Normal")
                            }
                        }
                    }
                }

                // SLA alerts
                Card(shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SLA & alerts", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.height(8.dp))
                        AlertRow("Courier offline mid-route", severity = AlertSeverity.HIGH)
                        AlertRow("Delivery #D-2041 exceeded SLA by 45 min", severity = AlertSeverity.MEDIUM)
                        AlertRow("Payment conflict for order #D-2033", severity = AlertSeverity.LOW)
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    delta: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (delta.isNotEmpty()) {
                Text(delta, fontSize = 11.sp, color = Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
fun DeliveryTableHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("Tracking ID", "Customer", "Courier", "Status", "Payment").forEachIndexed { i, col ->
            Text(
                col,
                modifier = Modifier.weight(if (i == 0) 1.5f else 1f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DeliveryRow(
    trackingId: String,
    customer: String,
    courier: String,
    status: String,
    paymentStatus: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(trackingId.take(10), modifier = Modifier.weight(1.5f), fontSize = 12.sp)
        Text(customer.take(12), modifier = Modifier.weight(1f), fontSize = 12.sp)
        Text(courier.take(12), modifier = Modifier.weight(1f), fontSize = 12.sp)
        Box(modifier = Modifier.weight(1f)) { StatusBadge(status) }
        Box(modifier = Modifier.weight(1f)) { PaymentBadge(paymentStatus) }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status.uppercase()) {
        "DELIVERED" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "INTRANSIT", "IN_TRANSIT" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        "DELAYED" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        "CANCELED" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        "PROCESSING" -> Color(0xFFF3E5F5) to Color(0xFF6A1B9A)
        else -> Color(0xFFF5F5F5) to Color(0xFF424242)
    }
    Surface(shape = RoundedCornerShape(4.dp), color = bg, modifier = Modifier.wrapContentSize()) {
        Text(status.lowercase().replace('_', ' '), color = fg, fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@Composable
fun PaymentBadge(status: String) {
    val (bg, fg) = if (status == "Paid") Color(0xFFE8F5E9) to Color(0xFF2E7D32)
    else Color(0xFFFFF3E0) to Color(0xFFE65100)
    Surface(shape = RoundedCornerShape(4.dp), color = bg, modifier = Modifier.wrapContentSize()) {
        Text(status, color = fg, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@Composable
fun DispatchQueueRow(orderId: String, priority: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Circle, null, tint = Color(0xFFFF9800), modifier = Modifier.size(8.dp))
        Spacer(Modifier.width(8.dp))
        Text(orderId.take(12), fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(priority, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        OutlinedButton(onClick = {}, modifier = Modifier.height(28.dp)) {
            Text("Assign", fontSize = 11.sp)
        }
    }
}

enum class AlertSeverity { HIGH, MEDIUM, LOW }

@Composable
fun AlertRow(message: String, severity: AlertSeverity) {
    val (tint, label) = when (severity) {
        AlertSeverity.HIGH -> Color(0xFFF44336) to "HIGH"
        AlertSeverity.MEDIUM -> Color(0xFFFF9800) to "MED"
        AlertSeverity.LOW -> Color(0xFF2196F3) to "LOW"
    }
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = RoundedCornerShape(3.dp), color = tint.copy(alpha = 0.12f)) {
            Text(label, color = tint, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text(message, fontSize = 12.sp, modifier = Modifier.weight(1f))
    }
}
