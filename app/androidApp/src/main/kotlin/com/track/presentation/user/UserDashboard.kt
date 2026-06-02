package com.track.presentation.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.track.domain.models.OrderStatus
import com.track.ui.components.MockTrackMap
import com.track.ui.theme.AppTheme

@Composable
fun UserDashboard(
    viewModel: UserViewModel = hiltViewModel(), // 👈 CRITICAL: Use hiltViewModel()
) {
    val currentJob by viewModel.currentJob.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Dashboard") },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
            )
        },
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (currentJob != null) {
            val job = currentJob!!
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Status Message (Snackbar style)
                if (statusMessage.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Text(
                            text = statusMessage,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                // Job Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Job ID: ${job.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            StatusChip(job.orderStatus)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Print,
                                contentDescription = "Printer",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                val firstItem = job.items.firstOrNull()
                                Text(firstItem?.productName ?: "Unknown Item", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "${firstItem?.machineType?.displayName ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }

                // Map (Uses MockTrackMap to avoid API Key issues)
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    MockTrackMap(
                        currentLocation = job.currentLocation,
                        locationHistory = job.locationHistory,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // Actions
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.startTransit(job.id) },
                        enabled = job.orderStatus == OrderStatus.PENDING,
                    ) {
                        Text("START TRANSIT")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.markDelivered(job.id) },
                        enabled = job.orderStatus == OrderStatus.INTRANSIT,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006600)),
                    ) {
                        Text("MARK DELIVERED")
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "No Jobs",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No active jobs assigned", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: OrderStatus) {
    val color =
        when (status) {
            OrderStatus.INTRANSIT -> Color.Blue
            OrderStatus.DELIVERED -> Color(0xFF006600)
            OrderStatus.PENDING -> Color(0xFFFFC107)
            else -> Color.Gray
        }
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.1f)) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Preview(showBackground = true, name = "User Dashboard")
@Composable
fun UserDashboardPreview() {
    AppTheme {
        // Removed broken ViewModel instantiation that caused KSP processing error
        UserDashboard()
    }
}
