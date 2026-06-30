package com.track.presentation.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShopBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    cartCount: Int
) {
    Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(Icons.Default.Home, "Home", selectedTab == 0) { onTabSelected(0) }
                BottomNavItem(Icons.Default.GridView, "Analytics", selectedTab == 1) { onTabSelected(1) }
                Spacer(Modifier.width(60.dp)) // Space for floating button
                BottomNavItem(Icons.Default.AccountBalanceWallet, "Finance", selectedTab == 3) { onTabSelected(3) }
                BottomNavItem(Icons.Default.Person, "Profile", selectedTab == 4) { onTabSelected(4) }
            }
        }
        
        // Floating Cart Button
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 0.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(8.dp, CircleShape)
                    .clickable { onTabSelected(2) },
                shape = CircleShape,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape,
                        color = if (selectedTab == 2) ShopPrimary else Color(0xFF311B92)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ShoppingBag, null, tint = Color.White)
                        }
                    }
                }
            }
            
            if (cartCount > 0) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).size(20.dp).offset(x = 4.dp, y = 4.dp),
                    shape = CircleShape,
                    color = ShopPrimary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("$cartCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Icon(
            icon, 
            contentDescription = label, 
            tint = if (isSelected) ShopPrimary else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            label, 
            fontSize = 10.sp, 
            color = if (isSelected) ShopPrimary else Color.Gray
        )
    }
}
