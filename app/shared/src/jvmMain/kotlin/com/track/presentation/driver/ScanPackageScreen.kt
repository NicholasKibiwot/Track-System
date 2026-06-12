package com.track.presentation.driver

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanPackageScreen(
    onScanSuccess: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var manualMode by remember { mutableStateOf(false) }
    var trackingIdInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (manualMode) "Manual Entry" else "Scan Package") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!manualMode) {
                // Mock Camera View
                Card(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(16.dp))
                            Text("Point camera at barcode", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                Button(
                    onClick = { onScanSuccess("TRK-MOCK-123") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SIMULATE SCAN")
                }
                
                TextButton(onClick = { manualMode = true }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Enter Tracking ID Manually")
                }
            } else {
                Text(
                    "Outsourced Driver Manual Entry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "For manual delivery confirmation without scanning.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = trackingIdInput,
                    onValueChange = { trackingIdInput = it },
                    label = { Text("Tracking Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("e.g. ABC12345") }
                )
                
                Spacer(Modifier.height(24.dp))
                
                Button(
                    onClick = { onScanSuccess(trackingIdInput) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = trackingIdInput.length >= 6
                ) {
                    Text("CONFIRM DELIVERY")
                }
                
                TextButton(onClick = { manualMode = false }) {
                    Text("Back to Scanner")
                }
            }
        }
    }
}
