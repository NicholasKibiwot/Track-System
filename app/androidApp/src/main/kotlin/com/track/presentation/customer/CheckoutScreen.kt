package com.track.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CheckoutScreen(
    onOrderSuccess: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    var deliveryType by remember { mutableStateOf("COMPANY") }
    var paymentMethod by remember { mutableStateOf("CARD") }
    var address by remember { mutableStateOf("Jane Doe\n3 Bridge-court\nChino Hills, CA 91709, United States") }

    val cartTotal = viewModel.cartTotal
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Shipping Address Section
            Column {
                Text("Shipping address", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Jane Doe", fontWeight = FontWeight.Medium)
                            Text("Change", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable {})
                        }
                        Text(
                            address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Payment Section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Change", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable {})
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(64.dp, 38.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("MasterCard", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("**** **** **** 3947", modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight.Medium)
                }
            }

            // Delivery Method Section
            Column {
                Text("Delivery method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StoreDeliveryCard(Modifier.weight(1f), "FedEx", "2-3 days")
                    StoreDeliveryCard(Modifier.weight(1f), "DHL", "1-2 days")
                }
            }

            // Summary Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Order:", color = Color.Gray)
                    Text("KES $cartTotal", fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delivery:", color = Color.Gray)
                    Text("KES 500", fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Summary:", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("KES ${cartTotal + 500}", fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.placeOrder(
                        paymentMethod = paymentMethod,
                        origin = "Central Warehouse",
                        destination = address,
                        deliveryType = deliveryType,
                        onSuccess = onOrderSuccess
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("SUBMIT ORDER", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StoreDeliveryCard(modifier: Modifier, title: String, time: String) {
    Surface(
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(time, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
