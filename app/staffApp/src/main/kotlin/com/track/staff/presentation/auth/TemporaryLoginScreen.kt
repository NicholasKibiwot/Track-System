package com.track.staff.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TemporaryLoginScreen(
    onNavigateToAdmin: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToDriver: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1C1E))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Track Staff Debug Portal",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Select a role to bypass login",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        RoleButton(
            title = "Admin Dashboard",
            subtitle = "Control Panel & Management",
            icon = Icons.Default.AdminPanelSettings,
            color = Color(0xFFE91E63),
            onClick = onNavigateToAdmin
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleButton(
            title = "Staff Dashboard",
            subtitle = "Inventory & Order Lookup",
            icon = Icons.Default.Badge,
            color = Color(0xFF2196F3),
            onClick = onNavigateToStaff
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleButton(
            title = "Driver Panel",
            subtitle = "Deliveries & Scanning",
            icon = Icons.Default.LocalShipping,
            color = Color(0xFF4CAF50),
            onClick = onNavigateToDriver
        )
    }
}

@Composable
private fun RoleButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
            
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
        }
    }
}

@Preview
@Composable
fun TemporaryLoginScreenPreview() {
    TemporaryLoginScreen({}, {}, {})
}
