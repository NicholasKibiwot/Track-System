package com.track.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.presentation.viewmodel.AppAuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AppAuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { /* Navigate to Home via parent */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Timeline, contentDescription = null) },
                    label = { Text("Tracking") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("History") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = true,
                    onClick = { }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // User Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = currentUser?.name?.ifBlank { "User Name" } ?: "User Name",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = currentUser?.email?.ifBlank { "email@example.com" } ?: "email@example.com",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                Button(
                    onClick = onNavigateToEditProfile,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C84FF)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Management", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            // Management List
            SettingsListItem(
                icon = Icons.Default.ShoppingBag, 
                title = "My Orders", 
                onClick = onNavigateToOrders
            )
            SettingsListItem(
                icon = Icons.Default.LocationOn, 
                title = "Shipping Address"
            )
            SettingsListItem(
                icon = Icons.Default.Payment, 
                title = "Payment Methods"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            SettingsListItem(icon = Icons.Default.Notifications, title = "Notifications")
            SettingsListItem(icon = Icons.Default.Lock, title = "Security")
            SettingsListItem(icon = Icons.Default.Help, title = "Support Center")
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsListItem(
                icon = Icons.AutoMirrored.Filled.Logout, 
                title = "Logout", 
                onClick = onLogout, 
                tint = Color.Red
            )
        }
    }
}

@Composable
fun SettingsListItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {},
    tint: Color = Color.Black
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                modifier = Modifier.size(24.dp), 
                tint = if (tint == Color.Red) tint else tint.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, modifier = Modifier.weight(1f), fontSize = 15.sp, color = tint)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
