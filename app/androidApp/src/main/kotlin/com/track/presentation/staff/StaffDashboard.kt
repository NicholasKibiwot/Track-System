package com.track.presentation.staff

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StaffDashboard() {
    var trackingInput by remember { mutableStateOf("") }
    var showLookup by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Staff Help Desk") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = trackingInput,
                onValueChange = { trackingInput = it },
                label = { Text("Enter Tracking ID") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = { showLookup = trackingInput.isNotBlank() }, modifier = Modifier.fillMaxWidth()) {
                Text("Lookup Order")
            }

            if (showLookup) {
                OrderLookupScreen(trackingId = trackingInput)
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Outsourced Delivery Update", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Tracking ID") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { /* Mark Delivered */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Mark as Delivered (No Scan)")
                    }
                }
            }
        }
    }
}
