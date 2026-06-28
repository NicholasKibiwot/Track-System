package com.track.presentation.staff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.track.util.kmpViewModel
import com.track.data.FakeData
import com.track.models.Order
import com.track.models.OrderItem
import com.track.models.PaymentStatus

@Composable
fun OrderLookupScreen(
    trackingId: String? = null,
    viewModel: StaffViewModel = kmpViewModel()
) {
    val lookupResult by viewModel.lookupResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var searchInput by remember { mutableStateOf(trackingId ?: "") }

    LaunchedEffect(trackingId) {
        if (!trackingId.isNullOrBlank()) {
            viewModel.lookupOrder(trackingId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Order Lookup") })
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Search bar row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Tracking ID or Order ID") },
                    singleLine = true,
                )
                Button(
                    onClick = {
                        if (searchInput.isNotBlank()) {
                            viewModel.lookupOrder(searchInput.trim())
                        }
                    },
                ) {
                    Text("Search")
                }
            }

            // Content area
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = errorMessage ?: "An error occurred.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                lookupResult != null -> {
                    OrderLookupResult(order = lookupResult!!)
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Enter a tracking or order ID above to search.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderLookupResult(order: Order) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Order: ${order.id}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = order.orderStatus.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            HorizontalDivider()

            // Order info
            Text(
                text = "Tracking No: ${order.trackingNumber}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Customer: ${order.customerName}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Origin: ${order.origin}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Destination: ${order.destination}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Driver: ${order.driverName ?: "Not assigned"}",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "Current Location: ${order.currentLocation?.address ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )

            HorizontalDivider()
            Spacer(Modifier.height(4.dp))

            // Items
            Text(
                text = "Items",
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(4.dp))

            order.items.forEach { item: OrderItem ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${item.quantity}x ${item.productName}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "KES ${item.unitPrice * item.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            HorizontalDivider()
            Spacer(Modifier.height(4.dp))

            // Totals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = "KES ${order.totalAmount}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(4.dp))

            // Payment info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = order.paymentMethod?.name ?: "N/A",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Payment Status",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = order.paymentStatus.name,
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        when (order.paymentStatus) {
                            PaymentStatus.PAID -> MaterialTheme.colorScheme.primary
                            PaymentStatus.PENDING -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }

            Spacer(Modifier.height(4.dp))

            // Delivery info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Delivery Type",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = order.deliveryType,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderLookupScreenPreview() {
    MaterialTheme {
        OrderLookupScreen()
    }
}

