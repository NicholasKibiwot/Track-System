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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onOrderSuccess: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    var deliveryType by remember { mutableStateOf("COMPANY") }
    var paymentMethod by remember { mutableStateOf("CARD") }
    
    // Real Address Fields
    var fullName by remember { mutableStateOf("") }
    var streetAddress by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val cartTotal = viewModel.cartTotal
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Shipping Address Section
            Text("Shipping Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = streetAddress,
                onValueChange = { streetAddress = it },
                label = { Text("Street Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Payment Method Section
            Text("Payment Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentMethodCard(
                    modifier = Modifier.weight(1f),
                    isSelected = paymentMethod == "CARD",
                    label = "Card",
                    icon = Icons.Default.CreditCard,
                    onClick = { paymentMethod = "CARD" }
                )
                PaymentMethodCard(
                    modifier = Modifier.weight(1f),
                    isSelected = paymentMethod == "MOMO",
                    label = "M-Pesa",
                    icon = Icons.Default.Smartphone,
                    onClick = { paymentMethod = "MOMO" }
                )
                PaymentMethodCard(
                    modifier = Modifier.weight(1f),
                    isSelected = paymentMethod == "CASH",
                    label = "Cash",
                    icon = Icons.Default.Payments,
                    onClick = { paymentMethod = "CASH" }
                )
            }

            // Delivery Method Section
            Text("Delivery Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DeliveryCard(
                    modifier = Modifier.weight(1f),
                    title = "Standard",
                    time = "2-3 days",
                    isSelected = deliveryType == "COMPANY",
                    onClick = { deliveryType = "COMPANY" }
                )
                DeliveryCard(
                    modifier = Modifier.weight(1f),
                    title = "Express",
                    time = "1-2 days",
                    isSelected = deliveryType == "OUTSOURCED",
                    onClick = { deliveryType = "OUTSOURCED" }
                )
            }

            // Summary Section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Items Total:", color = Color.Gray)
                        Text("KES $cartTotal", fontWeight = FontWeight.Medium)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Fee:", color = Color.Gray)
                        val fee = if (deliveryType == "OUTSOURCED") 800 else 400
                        Text("KES $fee", fontWeight = FontWeight.Medium)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Payable:", fontWeight = FontWeight.Bold)
                        val fee = if (deliveryType == "OUTSOURCED") 800 else 400
                        Text("KES ${cartTotal + fee}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                    }
                }
            }

            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    val fullAddress = "$fullName, $streetAddress, $city. Tel: $phone"
                    viewModel.placeOrder(
                        paymentMethod = paymentMethod,
                        origin = "Main Warehouse",
                        destination = fullAddress,
                        deliveryType = deliveryType,
                        onSuccess = onOrderSuccess
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && fullName.isNotBlank() && streetAddress.isNotBlank() && phone.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("PLACE ORDER", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PaymentMethodCard(
    modifier: Modifier,
    isSelected: Boolean,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier.clickable { onClick() },
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.outlinedCardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray)
            Text(label, style = MaterialTheme.typography.labelMedium, color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray)
        }
    }
}

@Composable
fun DeliveryCard(
    modifier: Modifier,
    title: String,
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier.height(80.dp).clickable { onClick() },
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.outlinedCardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black)
            Text(time, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
