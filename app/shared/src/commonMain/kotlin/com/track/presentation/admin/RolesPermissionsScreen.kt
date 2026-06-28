package com.track.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RoleDefinition(
    val name: String,
    val description: String,
    val permissions: Map<String, Boolean>
)

val defaultRoles = listOf(
    RoleDefinition(
        "Superadmin", "Full system control across all regions",
        mapOf(
            "View deliveries" to true, "Edit deliveries" to true, "Manage routes" to true,
            "Manage roles" to true, "Access system logs" to true, "Manage staff" to true,
            "Financial reports" to true, "System configuration" to true
        )
    ),
    RoleDefinition(
        "Admin", "Manage deliveries and staff within region",
        mapOf(
            "View deliveries" to true, "Edit deliveries" to true, "Manage routes" to false,
            "Manage roles" to false, "Access system logs" to false, "Manage staff" to true,
            "Financial reports" to true, "System configuration" to false
        )
    ),
    RoleDefinition(
        "Dispatcher", "Assign and track orders in real-time",
        mapOf(
            "View deliveries" to true, "Edit deliveries" to true, "Manage routes" to false,
            "Manage roles" to false, "Access system logs" to false, "Manage staff" to false,
            "Financial reports" to false, "System configuration" to false
        )
    ),
    RoleDefinition(
        "Courier", "Pickup and deliver packages",
        mapOf(
            "View deliveries" to true, "Edit deliveries" to false, "Manage routes" to false,
            "Manage roles" to false, "Access system logs" to false, "Manage staff" to false,
            "Financial reports" to false, "System configuration" to false
        )
    ),
    RoleDefinition(
        "Support", "Customer-facing assistance and order lookups",
        mapOf(
            "View deliveries" to true, "Edit deliveries" to false, "Manage routes" to false,
            "Manage roles" to false, "Access system logs" to false, "Manage staff" to false,
            "Financial reports" to false, "System configuration" to false
        )
    )
)

@Composable
fun RolesPermissionsScreen() {
    var selectedRole by remember { mutableStateOf(defaultRoles.first()) }
    var roles by remember { mutableStateOf(defaultRoles) }

    Row(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Left: role list
        Card(
            modifier = Modifier.width(240.dp).fillMaxHeight(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Roles", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                roles.forEach { role ->
                    val selected = role.name == selectedRole.name
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface,
                        onClick = { selectedRole = role }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(role.name, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text(role.description, fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right: permission matrix
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(selectedRole.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(selectedRole.description, fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(20.dp))
                Text("Permissions", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(Modifier.height(12.dp))

                var mutablePerms by remember(selectedRole) {
                    mutableStateOf(selectedRole.permissions.toMutableMap())
                }

                mutablePerms.forEach { (perm, enabled) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(perm, fontSize = 13.sp)
                        Switch(
                            checked = enabled,
                            onCheckedChange = { checked ->
                                mutablePerms = (mutablePerms + (perm to checked)).toMutableMap()
                            }
                        )
                    }
                    HorizontalDivider()
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        val updatedRole = selectedRole.copy(permissions = mutablePerms.toMap())
                        roles = roles.map { if (it.name == selectedRole.name) updatedRole else it }
                        selectedRole = updatedRole
                    }
                ) {
                    Text("Update role")
                }
            }
        }
    }
}
