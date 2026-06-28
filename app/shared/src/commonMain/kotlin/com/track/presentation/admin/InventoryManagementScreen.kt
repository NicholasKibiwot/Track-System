package com.track.presentation.admin

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.track.util.kmpViewModel
import com.track.domain.models.Product

@Composable
fun InventoryManagementScreen(
    viewModel: SuperAdminViewModel = kmpViewModel()
) {
    val products by viewModel.products.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Current Inventory", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
            }
            items(products) { product ->
                InventoryItemRow(product = product)
            }
        }
    }

    if (showAddDialog) {
        InventoryAddDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newProduct ->
                viewModel.createProduct(newProduct)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun InventoryAddDialog(
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = branch, onValueChange = { branch = it }, label = { Text("Branch Location") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(Product(id = "", name = name, price = price.toDoubleOrNull() ?: 0.0, stock = stock.toIntOrNull() ?: 0, branch = branch))
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun InventoryItemRow(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val displayImageUrl = product.images.firstOrNull()?.storage?.webpUrl 
                ?: product.images.firstOrNull()?.storage?.webpPath 
                ?: product.imageUrl

            SubcomposeAsyncImage(
                model = displayImageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop,
                error = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Print, contentDescription = null, tint = Color.LightGray)
                    }
                }
            )
            
            Spacer(Modifier.width(16.dp))
            
            Column(Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("KES ${product.price}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("Branch: ${product.branch.ifBlank { "Nairobi HQ" }}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = if (product.stock < 5) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "Stock: ${product.stock}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = if (product.stock < 5) Color.Red else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(product.category.replace("_", " "), fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

