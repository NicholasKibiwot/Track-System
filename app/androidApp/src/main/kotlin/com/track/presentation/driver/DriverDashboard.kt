package com.track.presentation.driver

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.track.data.FakeData
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.presentation.driver.DriverViewModel

@Composable
fun DriverDashboard(
    viewModel: DriverViewModel = hiltViewModel(),
    onScanPackage: () -> Unit = {},
) {
    val assignedOrders by viewModel.assignedOrders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Driver Panel") })
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Scan button at top
            Button(
                onClick = onScanPackage,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
            ) {
                Text("Scan Package for Pickup")
            }

            Text(
                text = "Assigned Deliveries (${assignedOrders.size})",
                style = MaterialTheme.typography.titleMedium,
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                assignedOrders.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No deliveries assigned yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(assignedOrders) { order: Order ->
                            DriverOrderCard(
                                order = order,
                                onUpdateLocation = {
                                    viewModel.simulateLocationUpdate(order.id)
                                },
                                onMarkDelivered = {
                                    viewModel.updateOrderStatus(
                                        order.id,
                                        OrderStatus.DELIVERED,
                                    )
                                },
                                onMarkInTransit = {
                                    viewModel.updateOrderStatus(
                                        order.id,
                                        OrderStatus.INTRANSIT,
                                    )
                                },
                            )
                        }
                    }
                }
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "TRK: ${order.trackingNumber}",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = order.orderStatus.name,
                    style = MaterialTheme.typography.labelMedium,
                    color =
                        when (order.orderStatus) {
                            OrderStatus.INTRANSIT -> MaterialTheme.colorScheme.tertiary
                            OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primary
                            OrderStatus.DELAYED -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }

            Divider()

            Text(
                text = "Customer: ${order.customerName}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "From: ${order.origin}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "To: ${order.destination}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Current: ${order.currentLocation?.address ?: "Location not set"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )

            Divider()

            // Items summary
            Text(
                text = "${order.items.size} item(s) — KES ${order.totalAmount}",
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(Modifier.height(4.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onUpdateLocation,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Update Location",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                if (order.orderStatus != OrderStatus.INTRANSIT) {
                    OutlinedButton(
                        onClick = onMarkInTransit,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "Mark In Transit",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                if (order.orderStatus != OrderStatus.DELIVERED) {
                    Button(
                        onClick = onMarkDelivered,
                        modifier = Modifier.weight(1f),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                    ) {
                        Text(
                            text = "Delivered",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DriverDashboardPreview() {
    Column(Modifier.padding(16.dp)) {
        DriverOrderCard(
            order = FakeData.previewOrder,
            onUpdateLocation = {},
            onMarkDelivered = {},
            onMarkInTransit = {},
        )
    }
}
