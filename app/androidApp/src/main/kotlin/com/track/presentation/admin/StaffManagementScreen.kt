package com.track.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.track.data.FakeData
import com.track.domain.models.User
import com.track.presentation.admin.SuperAdminViewModel

@Composable
fun StaffManagementScreen(viewModel: SuperAdminViewModel = hiltViewModel()) {
    val staffUsers by viewModel.staffUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    StaffManagementContent(
        staffList = staffUsers,
        onToggleActive = { user ->
            viewModel.toggleStaffActive(user.id, !user.isActive)
        },
        onAddStaff = { /* navigate to add staff screen */ },
    )
}

@Composable
fun StaffManagementContent(
    staffList: List<User>,
    onToggleActive: (User) -> Unit,
    onAddStaff: () -> Unit,
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(staffList) { staff: User ->
            Card {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (staff.isActive) Color(0xFF4CAF50) else Color(0xFFF44336),
                                ),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(staff.name, style = MaterialTheme.typography.bodyLarge)
                        Text(staff.email, style = MaterialTheme.typography.bodySmall)
                        Text(
                            staff.role,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Button(onClick = { onToggleActive(staff) }) {
                        Text(if (staff.isActive) "Deactivate" else "Activate")
                    }
                }
            }
        }
        item {
            Button(
                onClick = onAddStaff,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Add New Staff")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StaffManagementScreenPreview() {
    StaffManagementContent(
        staffList =
            listOf(
                FakeData.previewStaffUser,
                FakeData.previewAdminUser,
            ),
        onToggleActive = {},
        onAddStaff = {},
    )
}
