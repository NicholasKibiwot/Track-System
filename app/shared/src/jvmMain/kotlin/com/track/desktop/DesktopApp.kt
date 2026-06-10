package com.track.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.desktop.customer.DesktopCustomerApp
import com.track.desktop.staff.DesktopStaffApp

@Composable
fun DesktopApp() {
    var selectedApp by remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        if (selectedApp == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1C1E)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Track Desktop Portal",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Choose the instance to launch",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        AppLaunchButton(
                            title = "Customer App",
                            icon = Icons.Default.ShoppingBag,
                            color = Color(0xFFFF9800),
                            onClick = { selectedApp = "customer" }
                        )
                        AppLaunchButton(
                            title = "Staff Portal",
                            icon = Icons.Default.Badge,
                            color = Color(0xFF2196F3),
                            onClick = { selectedApp = "staff" }
                        )
                    }
                }
            }
        } else {
            when (selectedApp) {
                "customer" -> DesktopCustomerApp(onBack = { selectedApp = null })
                "staff" -> DesktopStaffApp(onBack = { selectedApp = null })
            }
        }
    }
}

@Composable
fun AppLaunchButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
        contentPadding = PaddingValues(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = color.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
