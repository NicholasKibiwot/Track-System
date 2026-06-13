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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.util.kmpViewModel
import com.track.domain.models.User
import com.track.domain.models.UserRole

@Composable
fun StaffManagementScreen(
    viewModel: SuperAdminViewModel = kmpViewModel()
) {
    val staffUsers by viewModel.staffUsers.collectAsState()
    var showAddStaffDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddStaffDialog = true },
                containerColor = Color(0xFF1A1C1E),
                contentColor = Color.White
            ) {
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
                Text("Manage Company Staff", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Drivers and branch staff members", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
        AddStaffDialog(
            onDismiss = { showAddStaffDialog = false },
            onConfirm = { newUser ->
                viewModel.createUser(newUser)
                showAddStaffDialog = false
            }
        )
    }
}

@Composable
fun AddStaffDialog(
    onDismiss: () -> Unit,
    onConfirm: (User) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.STAFF) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Staff") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = branch, onValueChange = { branch = it }, label = { Text("Branch Location") }, modifier = Modifier.fillMaxWidth())
                
                Text("Role", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = role == UserRole.STAFF, onClick = { role = UserRole.STAFF })
                        Text("Staff")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = role == UserRole.DRIVER, onClick = { role = UserRole.DRIVER })
                        Text("Driver")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(User(name = name, email = email, branch = branch, role = role))
                },
                enabled = name.isNotBlank() && email.isNotBlank() && branch.isNotBlank()
            ) { Text("Create Account") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
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
                Text(staff.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(staff.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(staff.role.name, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(" • ", fontSize = 10.sp, color = Color.Gray)
                    Text(staff.branch.ifBlank { "Unassigned Branch" }, fontSize = 10.sp, color = Color.Gray)
                }
            }
            
            Switch(
                checked = staff.isActive,
                onCheckedChange = { onToggleActive() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4CAF50),
                    checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                )
            )
        }
    }
}

