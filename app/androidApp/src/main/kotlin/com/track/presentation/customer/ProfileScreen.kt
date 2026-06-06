package com.track.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.track.domain.models.UserRole
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
                    onClick = { }
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
                            .background(Color.LightGray)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = currentUser?.name ?: "User Name",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = currentUser?.email ?: "email@example.com",
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

            Spacer(modifier = Modifier.height(24.dp))

            // Coupon Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Get 1 coupon for free shipping cost!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Let's complete your data and get our coupon",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = 0.7f,
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = Color(0xFF4C84FF),
                            trackColor = Color(0xFFE9ECEF)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF4E5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("%", color = Color(0xFFFF9800), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Extra Advantages & Swift Coin Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Extra Advantages",
                    subtitle = "SwiftShip Loyalty",
                    icon = Icons.Default.Verified,
                    iconTint = Color(0xFFFFC107)
                )
                SmallInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Swift Coin",
                    subtitle = "Get your swift coins!",
                    icon = Icons.Default.MonetizationOn,
                    iconTint = Color(0xFF4C84FF)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Referral Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CardGiftcard,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color(0xFF4C84FF)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Invite your friends!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            "Invite your friends using your referral link to unlock bonuses",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEBF2FF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("My Referral Code", color = Color(0xFF4C84FF), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Setting", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            // Settings List
            SettingsListItem(icon = Icons.Default.Lock, title = "Set PIN Payment")
            SettingsListItem(icon = Icons.Default.LocationOn, title = "Saved Address")
            SettingsListItem(icon = Icons.Default.Security, title = "Privacy Setting")
            SettingsListItem(icon = Icons.Default.Language, title = "Language")
            SettingsListItem(icon = Icons.Default.Payment, title = "Billing Payment")
            
            Spacer(modifier = Modifier.height(16.dp))
            SettingsListItem(icon = Icons.AutoMirrored.Filled.Logout, title = "Logout", onClick = onLogout, tint = Color.Red)
        }
    }
}

@Composable
fun SmallInfoCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = subtitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = tint.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, modifier = Modifier.weight(1f), fontSize = 15.sp, color = tint)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}

