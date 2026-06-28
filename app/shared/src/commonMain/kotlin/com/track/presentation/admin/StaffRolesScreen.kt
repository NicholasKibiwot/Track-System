package com.track.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StaffRolesScreen(
    viewModel: SuperAdminViewModel,
    section: SuperAdminSection
) {
    val staffUsers by viewModel.staffUsers.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    val displayList = when (section) {
        SuperAdminSection.COURIERS -> allUsers.filter { it.role == "courier" || it.role == "driver" }
        else -> staffUsers
    }
    val title = if (section == SuperAdminSection.COURIERS) "Couriers" else "Staff"

    var searchQuery by remember { mutableStateOf("") }
    val filtered = displayList.filter {
        searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true)
    }

    Row(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Left: staff list
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search $title…", fontSize = 12.sp) },
                    modifier = Modifier.width(200.dp).height(44.dp),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(16.dp)) },
                    shape = RoundedCornerShape(22.dp)
                )
            }
            Spacer(Modifier.height(16.dp))

            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column {
                    // Table header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        listOf("Name" to 2f, "Role" to 1f, "Status" to 1f, "Last active" to 1.2f, "Actions" to 1f)
                            .forEach { (col, w) ->
                                Text(col, modifier = Modifier.weight(w), fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                    }
                    HorizontalDivider()

                    if (filtered.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No $title found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 500.dp)) {
                            items(filtered) { user ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Avatar + name
                                    Row(
                                        modifier = Modifier.weight(2f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    user.name.take(2).uppercase(),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text(user.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                            Text(user.email, fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    RoleBadge(user.role, modifier = Modifier.weight(1f))
                                    // Active status
                                    Box(modifier = Modifier.weight(1f)) {
                                        val (statusBg, statusFg) = if (user.isActive)
                                            Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                                        else
                                            Color(0xFFFFEBEE) to Color(0xFFC62828)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = statusBg,
                                            modifier = Modifier.wrapContentSize()
                                        ) {
                                            Text(
                                                if (user.isActive) "Active" else "Suspended",
                                                color = statusFg,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text("–", modifier = Modifier.weight(1.2f), fontSize = 12.sp)
                                    // Action buttons
                                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {},
                                            modifier = Modifier.size(28.dp)
                                        ) { Icon(Icons.Default.Edit, null, Modifier.size(14.dp)) }
                                        IconButton(
                                            onClick = { viewModel.toggleStaffActive(user.id, !user.isActive) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                if (user.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                                                null, Modifier.size(14.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {},
                                            modifier = Modifier.size(28.dp)
                                        ) { Icon(Icons.Default.LockReset, null, Modifier.size(14.dp)) }
                                    }
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoleBadge(role: String, modifier: Modifier = Modifier) {
    val (bg, fg) = when (role.lowercase()) {
        "superadmin" -> Color(0xFFF3E5F5) to Color(0xFF6A1B9A)
        "admin" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        "dispatcher" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        "courier", "driver" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "support" -> Color(0xFFFCE4EC) to Color(0xFFC62828)
        else -> Color(0xFFF5F5F5) to Color(0xFF424242)
    }
    Box(modifier = modifier) {
        Surface(shape = RoundedCornerShape(4.dp), color = bg, modifier = Modifier.wrapContentSize()) {
            Text(
                role.replaceFirstChar { it.uppercaseChar() },
                color = fg,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
