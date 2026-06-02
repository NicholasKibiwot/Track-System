package com.track.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.track.domain.models.User

@Composable
fun StaffManagementScreen(
    viewModel: SuperAdminViewModel = hiltViewModel()
) {
    val staffUsers by viewModel.staffUsers.collectAsState()
    var showAddStaffDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddStaffDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Staff")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Manage Company Staff", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
            }
            items(staffUsers) { staff ->
                StaffMemberCard(
                    staff = staff,
                    onToggleActive = { viewModel.toggleStaffActive(staff.id, !staff.isActive) }
                )
            }
        }
    }

    if (showAddStaffDialog) {
        // Simple dialog for UI demo
        AlertDialog(
            onDismissRequest = { showAddStaffDialog = false },
            title = { Text("Add New Staff") },
            text = { Text("This would open a form to create a new staff account.") },
            confirmButton = { Button(onClick = { showAddStaffDialog = false }) { Text("OK") } }
        )
    }
}

@Composable
fun StaffMemberCard(staff: User, onToggleActive: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Indicator (Red/Green)
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (staff.isActive) Color(0xFF4CAF50) else Color(0xFFF44336))
            )
            
            Spacer(Modifier.width(16.dp))
            
            Column(Modifier.weight(1f)) {
                Text(staff.name, style = MaterialTheme.typography.titleMedium)
                Text(staff.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(staff.role.name, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
            }
            
            IconButton(onClick = onToggleActive) {
                Icon(
                    if (staff.isActive) Icons.Default.Person else Icons.Default.Delete,
                    contentDescription = null,
                    tint = if (staff.isActive) Color.Gray else Color.Red
                )
            }
        }
    }
}
