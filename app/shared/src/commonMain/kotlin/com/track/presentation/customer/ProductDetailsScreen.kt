package com.track.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.domain.models.Product
import com.track.presentation.viewmodel.AppCustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    viewModel: AppCustomerViewModel,
    onBackClick: () -> Unit,
    onAddToCart: (Product) -> Unit,
) {
    val product = viewModel.getProductById(productId) ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val sizeLabels = product.sizes
    val colorLabels = product.colors

    var selectedSize by remember { mutableStateOf(sizeLabels.firstOrNull() ?: "") }
    var selectedColor by remember { mutableStateOf(colorLabels.firstOrNull() ?: "") }

    Scaffold(
        topBar = { ProductDetailsTopBar(onBackClick) },
        bottomBar = { ProductDetailsBottomBar(product, onAddToCart) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ProductImagePlaceholder()
            Column(modifier = Modifier.padding(16.dp)) {
                ProductHeader(product)
                Spacer(modifier = Modifier.height(8.dp))
                ProductInfo(product)
                Spacer(modifier = Modifier.height(24.dp))
                if (sizeLabels.isNotEmpty()) {
                    SizeSelector(sizeLabels, selectedSize) { selectedSize = it }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                if (colorLabels.isNotEmpty()) {
                    ColorSelector(colorLabels, selectedColor) { selectedColor = it }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                ReviewsSection()
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Product Details") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { /* Add to favorites */ }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
            }
        },
    )
}

@Composable
private fun ProductDetailsBottomBar(product: Product, onAddToCart: (Product) -> Unit) {
    Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Total Price", color = Color.Gray, fontSize = 14.sp)
                Text(
                    "$${product.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier
                    .height(56.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add to Cart", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProductImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center,
    ) {
        Text("Product Image", color = Color.Gray)
    }
}

@Composable
private fun ProductHeader(product: Product) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            product.category,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(20.dp),
            )
            Text(
                " ${product.rating}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ProductInfo(product: Product) {
    Text(
        product.name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        "Product Details",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        product.description,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray
    )
}

@Composable
private fun SizeSelector(sizes: List<String>, selectedSize: String, onSizeSelected: (String) -> Unit) {
    Text(
        "Select Size",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(12.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(sizes) { size ->
            val isSelected = selectedSize == size
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(0xFFFF5252) else Color.White)
                    .border(
                        1.dp,
                        if (isSelected) Color(0xFFFF5252) else Color.LightGray,
                        RoundedCornerShape(8.dp),
                    )
                    .clickable { onSizeSelected(size) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    size,
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ColorSelector(colors: List<String>, selectedColor: String, onColorSelected: (String) -> Unit) {
    Text(
        "Select Color",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(12.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(colors) { colorStr ->
            val color = try {
                Color(android.graphics.Color.parseColor(colorStr))
            } catch (e: Exception) {
                Color.Gray
            }
            val isSelected = selectedColor == colorStr
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        if (isSelected) 2.dp else 0.dp,
                        Color(0xFFFF5252),
                        CircleShape,
                    )
                    .clickable { onColorSelected(colorStr) },
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewsSection() {
    Text(
        "Reviews",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        "No reviews yet.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
    )
}
