package com.track.presentation.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.util.kmpViewModel
import com.track.domain.models.OrderStatus
import com.track.ui.components.MockTrackMap

@Composable
fun UserDashboard(
    viewModel: UserViewModel = kmpViewModel()
) {
    val currentJob by viewModel.currentJob.collectAsState(null)
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showScanSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Hub") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (currentJob != null) {
            val job = currentJob!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Job Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Current Job: №${job.trackingNumber}", fontWeight = FontWeight.Bold)
                            Text(job.destination, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        StatusBadge(job.orderStatus)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Map View
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                ) {
                    MockTrackMap(
                        currentLocation = job.currentLocation,
                        locationHistory = job.locationHistory,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Action Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showScanSuccess = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("SCAN PACKAGE")
                    }
                    
                    Button(
                        onClick = { viewModel.markDelivered(job.id) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2AA952))
                    ) {
                        Text("COMPLETE")
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Waiting for assigned deliveries...", color = Color.Gray)
            }
        }
    }
    
    if (showScanSuccess) {
        AlertDialog(
            onDismissRequest = { showScanSuccess = false },
            title = { Text("Scan Successful") },
            text = { Text("Package location has been updated in real-time.") },
            confirmButton = { Button(onClick = { showScanSuccess = false }) { Text("Done") } }
        )
    }
}

@Composable
fun StatusBadge(status: OrderStatus) {
    Surface(
        color = Color(0xFFF5A623).copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color(0xFFF5A623),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

