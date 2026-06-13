package com.track.presentation.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    onCallDriver: (String) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TrackingTopBar(orderId = order.id, onBackClick = onBackClick)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                MockTrackMap(
                    currentLocation = order.currentLocation,
                    locationHistory = order.locationHistory,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                )
            }
            item { ShipmentProgress(order.orderStatus) }
            item { PackageDetailsCard(order) }
            item { DriverInfoCard(order, onCallDriver) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackingTopBar(orderId: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Tracking #$orderId", style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Composable
private fun ShipmentProgress(status: OrderStatus) {
    Column {
        Text("Shipment Progress", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        TimelineView(status)
    }
}

@Composable
private fun PackageDetailsCard(order: Order) {
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

@Composable
private fun DriverInfoCard(order: Order, onCallDriver: (String) -> Unit) {
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
            IconButton(onClick = { order.driverId?.let { onCallDriver(it) } }) {
                Icon(Icons.Default.Phone, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun TimelineView(status: OrderStatus) {
    val steps = listOf("Pending", "Processing", "In Transit", "Delivered")
    val currentStepIndex = getStepIndex(status)

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        steps.forEachIndexed { index, stepName ->
            TimelineStep(index, currentStepIndex, stepName)
            if (index < steps.size - 1) {
                TimelineConnector(index < currentStepIndex)
            }
        }
    }
}

private fun getStepIndex(status: OrderStatus): Int = when (status) {
    OrderStatus.PENDING -> 0
    OrderStatus.PROCESSING -> 1
    OrderStatus.INTRANSIT -> 2
    OrderStatus.DELIVERED -> 3
    else -> 2
}

@Composable
private fun TimelineStep(index: Int, currentStepIndex: Int, name: String) {
    val isCompleted = index <= currentStepIndex
    val isPassed = index < currentStepIndex

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isCompleted) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isPassed) {
                Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            name,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCompleted) MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.width(60.dp),
        )
    }
}

@Composable
private fun RowScope.TimelineConnector(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .height(2.dp)
            .weight(1f)
            .background(
                if (isCompleted) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            ),
    )
}

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

