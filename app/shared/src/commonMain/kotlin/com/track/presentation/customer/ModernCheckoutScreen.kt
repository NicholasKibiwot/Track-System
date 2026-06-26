package com.track.presentation.customer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.track.util.isWideScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernCheckoutScreen(
    onOrderSuccess: (orderId: String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: CustomerViewModel
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val totalAmount = cartItems.sumOf { it.product.price * it.quantity }
    val isWide = isWideScreen()

    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("CREDIT_CARD") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text("Secure Checkout", fontWeight = FontWeight.Bold) 
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.Close, null) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF0F2F2))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            val contentModifier = if (isWide) Modifier.width(900.dp).align(Alignment.CenterHorizontally) else Modifier.fillMaxWidth()
            
            Row(
                modifier = contentModifier.padding(if (isWide) 32.dp else 16.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Main Info
                Column(modifier = Modifier.weight(1.5f)) {
                    CheckoutStep(1, "Shipping Address") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = address, 
                                onValueChange = { address = it }, 
                                label = { Text("Street Address") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    
                    HorizontalDivider(Modifier.padding(vertical = 16.dp))
                    
                    CheckoutStep(2, "Payment Method") {
                        Column {
                            PaymentOption("Credit or Debit Card", Icons.Default.CreditCard, paymentMethod == "CREDIT_CARD") { paymentMethod = "CREDIT_CARD" }
                            PaymentOption("M-Pesa / Mobile Money", Icons.Default.Smartphone, paymentMethod == "MOBILE_MONEY") { paymentMethod = "MOBILE_MONEY" }
                            PaymentOption("Bank Transfer", Icons.Default.AccountBalance, paymentMethod == "BANK_TRANSFER") { paymentMethod = "BANK_TRANSFER" }
                        }
                    }
                    
                    HorizontalDivider(Modifier.padding(vertical = 16.dp))
                    
                    CheckoutStep(3, "Review items and shipping") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            cartItems.forEach { item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(50.dp).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Print, null, tint = Color.LightGray)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(item.product.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                        Text("Qty: ${item.quantity}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Text("$$${item.product.price * item.quantity}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Order Summary Sidebar (Desktop Only)
                if (isWide) {
                    OrderSummarySidebar(totalAmount, isLoading, address.isNotBlank()) {
                        viewModel.placeOrder(
                            paymentMethod = paymentMethod,
                            origin = "Global Distribution Center",
                            destination = "$address, $city ($phone)",
                            deliveryType = "EXPRESS",
                            onSuccess = onOrderSuccess
                        )
                    }
                }
            }
            
            // Mobile Bottom Bar
            if (!isWide) {
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Order Total:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("$$totalAmount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB12704))
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.placeOrder(paymentMethod, "Global", "$address, $city", "EXPRESS", onOrderSuccess)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEB123)),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isLoading && address.isNotBlank()
                        ) {
                            Text("Place Your Order", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutStep(number: Int, title: String, content: @Composable () -> Unit) {
    Row {
        Text("$number", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.width(30.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun PaymentOption(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Icon(icon, null, modifier = Modifier.padding(horizontal = 12.dp), tint = if (selected) Color.Black else Color.Gray)
        Text(title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun OrderSummarySidebar(total: Double, loading: Boolean, ready: Boolean, onPlace: () -> Unit) {
    Surface(
        modifier = Modifier.width(300.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Button(
                onClick = onPlace,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEB123)),
                shape = RoundedCornerShape(8.dp),
                enabled = !loading && ready
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.Black)
                else Text("Place your order", color = Color.Black)
            }
            
            Text(
                "By placing your order, you agree to Printer Pro's privacy notice and conditions of use.",
                fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 12.dp)
            )
            
            HorizontalDivider()
            
            Text("Order Summary", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
            
            SummaryRow("Items:", "$$total")
            SummaryRow("Shipping & handling:", "$0.00")
            SummaryRow("Total before tax:", "$$total")
            SummaryRow("Estimated tax:", "$0.00")
            
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order total:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB12704))
                Text("$$total", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB12704))
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp)
        Text(value, fontSize = 12.sp)
    }
}
