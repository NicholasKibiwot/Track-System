package com.track.presentation.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.track.util.kmpViewModel
import com.track.models.Product
import com.track.models.ProductCategory

@Composable
fun AdminAddProductScreen(
    viewModel: SuperAdminViewModel = kmpViewModel(),
    onBackClick: () -> Unit = {},
    onProductAdded: () -> Unit = {},
) {
    val isLoading by viewModel.isLoading.collectAsState()

    // Refactored to separate state from content to allow Preview rendering
    AdminAddProductContent(
        isLoading = isLoading,
        onBackClick = onBackClick,
        onSaveProduct = { product ->
            viewModel.createProduct(product) {
                onProductAdded()
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddProductContent(
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onSaveProduct: (Product) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var sizes by remember { mutableStateOf("") } // comma separated
    var colors by remember { mutableStateOf("") } // comma separated Hex

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Product") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price ($)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = sizes,
                onValueChange = { sizes = it },
                label = { Text("Sizes (comma separated: S,M,L)") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = colors,
                onValueChange = { colors = it },
                label = { Text("Colors (comma separated Hex: #FF0000,#00FF00)") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val newProduct =
                        Product(
                            name = name,
                            description = description,
                            price = price.toDoubleOrNull() ?: 0.0,
                            category = ProductCategory.entries.find { it.name == category || it.displayName == category } ?: ProductCategory.OFFICE_PRINTERS,
                            stock = stock.toIntOrNull() ?: 0,
                            imageUrl = imageUrl,
                            sizes = sizes.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            colors = colors.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            rating = 4.5, // Default for new items
                        )
                    onSaveProduct(newProduct)
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                enabled = name.isNotBlank() && price.isNotBlank() && !isLoading,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Product")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminAddProductScreenPreview() {
    MaterialTheme {
        // Calling the Content composable directly in Preview to avoid ViewModel instantiation issues
        AdminAddProductContent(
            isLoading = false,
            onBackClick = {},
            onSaveProduct = {},
        )
    }
}
