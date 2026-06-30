package com.track.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import com.track.presentation.customer.CustomerViewModel
import com.track.util.kmpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopCartScreen(
    viewModel: CustomerViewModel = kmpViewModel<CustomerViewModel>(),
    onBackClick: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalAmount = viewModel.cartTotal
    val cartCount = cartItems.sumOf { it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = ShopPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("T", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Track Shop", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Creative Commerce", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, null, tint = Color.Gray) }
                    IconButton(onClick = {}) { Icon(Icons.Default.WbSunny, null, tint = Color.Gray) }
                    IconButton(onClick = {}) { Icon(Icons.Default.NotificationsNone, null, tint = Color.Gray) }
                }
            )
        },
        bottomBar = {
            ShopBottomNav(
                selectedTab = 2,
                onTabSelected = { 
                    when(it) {
                        0 -> onBackClick()
                        1 -> {}
                        2 -> {}
                        3 -> { /* Navigate to orders */ }
                        4 -> { /* Navigate to profile */ }
                    }
                },
                cartCount = cartCount
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ShopBackground)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Your cart,", color = ShopPrimary, fontSize = 14.sp)
                    Text("${cartItems.sumOf { it.quantity }} Items", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF311B92))
                }

                items(cartItems) { item ->
                    CartItemCard(item, viewModel)
                }
            }

            CartSummaryCard(totalAmount, onNavigateToCheckout)
        }
    }
}

@Composable
fun CartItemCard(item: CartItem, viewModel: CustomerViewModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val imageUrl = item.product.images.firstOrNull()?.storage?.webpUrl ?: item.product.imageUrl
            AsyncImage(
                model = imageUrl,
                contentDescription = item.product.name,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF311B92))
                    Text("$ ${item.product.price}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Text("1 unit", fontSize = 12.sp, color = Color.Gray)
                
                Spacer(Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("-", modifier = Modifier.clickable { viewModel.decreaseQuantity(item.product.id) }.padding(8.dp), color = ShopPrimary, fontWeight = FontWeight.Bold)
                    Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                    Text("+", modifier = Modifier.clickable { viewModel.increaseQuantity(item.product.id) }.padding(8.dp), color = ShopPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CartSummaryCard(total: Double, onPayNow: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            ShopSummaryRow("Sub Total", "$ $total")
            ShopSummaryRow("Discount", "$ 5.00")
            ShopSummaryRow("Shipping Charge", "FREE")
            ShopSummaryRow("Gift Packaging", "00.00")
            
            Spacer(Modifier.height(24.dp))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("$ ${total - 5.0}", fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Color.Black)
                Text("Total Payable", color = Color.Gray, fontSize = 14.sp)
                
                Spacer(Modifier.height(16.dp))
                
                Button(
                    onClick = onPayNow,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShopPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Pay Now", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, null)
                    }
                }
            }
        }
    }
}

@Composable
fun ShopSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
