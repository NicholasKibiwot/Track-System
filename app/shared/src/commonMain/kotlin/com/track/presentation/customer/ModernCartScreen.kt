package com.track.presentation.customer

import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.util.isWideScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernCartScreen(
    viewModel: CustomerViewModel,
    onNavigateToCheckout: () -> Unit,
    onBackClick: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val isWide = isWideScreen()
    val totalAmount = cartItems.sumOf { it.product.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            EmptyCartView(onBackClick, padding)
        } else {
            if (isWide) {
                WideCartLayout(cartItems, totalAmount, onNavigateToCheckout, viewModel, padding)
            } else {
                MobileCartLayout(cartItems, totalAmount, onNavigateToCheckout, viewModel, padding)
            }
        }
    }
}

@Composable
fun EmptyCartView(onBackClick: () -> Unit, padding: PaddingValues) {
    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(100.dp), tint = Color.LightGray)
            Spacer(Modifier.height(16.dp))
            Text("Your Printer Pro cart is empty", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Check your saved items or continue shopping", color = Color.Gray)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEB123)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Shop Today's Deals", color = Color.Black)
            }
        }
    }
}

@Composable
fun WideCartLayout(
    items: List<CartItem>,
    total: Double,
    onCheckout: () -> Unit,
    viewModel: CustomerViewModel,
    padding: PaddingValues
) {
    Row(
        modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFEAEDED)).padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Items List
        Surface(
            modifier = Modifier.weight(1f),
            color = Color.White,
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("Shopping Cart", fontSize = 28.sp, fontWeight = FontWeight.Medium)
                Text("Price", modifier = Modifier.align(Alignment.End), color = Color.Gray, fontSize = 14.sp)
                HorizontalDivider(Modifier.padding(vertical = 12.dp))
                
                LazyColumn {
                    items(items) { item ->
                        ModernCartItemRow(item, viewModel)
                        HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    }
                }
                
                Text(
                    "Subtotal (${items.sumOf { it.quantity }} items): $$total",
                    modifier = Modifier.align(Alignment.End),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Checkout Sidebar
        Column(modifier = Modifier.width(300.dp)) {
            Surface(color = Color.White, shape = RoundedCornerShape(4.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF067D62), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Your order qualifies for FREE Shipping", color = Color(0xFF067D62), fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Subtotal (${items.sumOf { it.quantity }} items): $$total",
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEB123)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Proceed to Checkout", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun MobileCartLayout(
    items: List<CartItem>,
    total: Double,
    onCheckout: () -> Unit,
    viewModel: CustomerViewModel,
    padding: PaddingValues
) {
    Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF7F7F7))) {
        LazyColumn(modifier = Modifier.weight(1f).padding(12.dp)) {
            items(items) { item ->
                ModernCartItemRow(item, viewModel)
                Spacer(Modifier.height(12.dp))
            }
        }
        
        Surface(color = Color.White, shadowElevation = 8.dp) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", fontSize = 18.sp)
                    Text("$$total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onCheckout,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEB123)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Proceed to Checkout", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ModernCartItemRow(item: CartItem, viewModel: CustomerViewModel) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        // Image
        Box(
            modifier = Modifier.size(120.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFF8F8F8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Print, null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
        }
        
        Spacer(Modifier.width(16.dp))
        
        Column(Modifier.weight(1f)) {
            Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)
            Text("In Stock", color = Color(0xFF007600), fontSize = 12.sp)
            Text("Eligible for FREE Shipping", color = Color.Gray, fontSize = 12.sp)
            
            Spacer(Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quantity Selector
                Surface(
                    border = BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF0F2F2)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        IconButton(onClick = { viewModel.removeFromCart(item.product.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
                        }
                        Text("Qty: ${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp), fontSize = 13.sp)
                        IconButton(onClick = { viewModel.addToCart(item.product) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                Spacer(Modifier.width(16.dp))
                
                Text(
                    "Delete", 
                    color = Color(0xFF007185), 
                    fontSize = 12.sp, 
                    modifier = Modifier.clickable { /* Logic to remove completely */ }
                )
                
                Spacer(Modifier.width(16.dp))
                
                Text(
                    "Save for later", 
                    color = Color(0xFF007185), 
                    fontSize = 12.sp, 
                    modifier = Modifier.clickable { }
                )
            }
        }
        
        Text("$$${item.product.price}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}
