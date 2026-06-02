package com.track.presentation.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.track.data.FakeData
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.ui.components.MockTrackMap
import com.track.ui.theme.*

@Composable
fun LiveTrackingScreen(
    order: Order,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tracking #${order.id}", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // 1. Mock Map (No API Key Needed)
            item {
                MockTrackMap(
                    currentLocation = order.currentLocation,
                    locationHistory = order.locationHistory,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                )
            }

            // 2. Status Timeline
            item {
                Text("Shipment Progress", style = MaterialTheme.typography.titleSmall)
                TimelineView(order.orderStatus)
            }

            // 3. Machine Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Package Details", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Print,
                                contentDescription = "Printer",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                val firstItem = order.items.firstOrNull()
                                Text(firstItem?.productName ?: "Unknown Item", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    "${firstItem?.machineType?.displayName ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }

            // 4. Driver Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Driver",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                order.driverName ?: "Driver Assigned",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(order.driverId ?: "ID: ---", style = MaterialTheme.typography.labelSmall)
                        }
                        IconButton(onClick = { /* TODO: Call Driver */ }) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// COMPONENTS
// ==========================================

@Composable
private fun TimelineView(status: OrderStatus) {
    val steps = listOf("Pending", "Processing", "In Transit", "Delivered")
    val currentStepIndex =
        when (status) {
            OrderStatus.PENDING -> 0
            OrderStatus.PROCESSING -> 1
            OrderStatus.INTRANSIT -> 2
            OrderStatus.DELIVERED -> 3
            else -> 2
        }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        steps.forEachIndexed { index, stepName ->
            // Step Indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (index <=
                                    currentStepIndex
                                ) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                        .copy(alpha = 0.2f)
                                },
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (index < currentStepIndex) {
                        Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stepName,
                    style = MaterialTheme.typography.labelSmall,
                    color =
                        if (index <=
                            currentStepIndex
                        ) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        },
                    modifier = Modifier.width(60.dp),
                )
            }

            // Connecting Line
            if (index < steps.size - 1) {
                Box(
                    modifier =
                        Modifier
                            .height(2.dp)
                            .weight(1f)
                            .background(
                                if (index <
                                    currentStepIndex
                                ) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                        .copy(alpha = 0.2f)
                                },
                            ),
                )
            }
        }
    }
}

// ==========================================
// PREVIEW
// ==========================================

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5, name = "Live Tracking Screen")
@Composable
private fun LiveTrackingScreenPreview() {
    AppTheme {
        LiveTrackingScreen(
            order = FakeData.previewOrders.first(),
            onBackClick = {},
        )
    }
}
