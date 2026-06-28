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
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.models.Product
import com.track.presentation.customer.CustomerViewModel
import com.track.util.isWideScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    viewModel: CustomerViewModel,
    onBackClick: () -> Unit,
    onAddToCart: (Product) -> Unit,
) {
    val product = viewModel.getProductById(productId) ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isWide = isWideScreen()
    val sizeLabels = product.sizes
    val colorLabels = product.colors

    var selectedSize by remember { mutableStateOf(sizeLabels.firstOrNull() ?: "") }
    var selectedColor by remember { mutableStateOf(colorLabels.firstOrNull() ?: "") }

    Scaffold(
        topBar = { ProductDetailsTopBar(onBackClick) },
        bottomBar = { 
            if (!isWide) {
                ProductDetailsBottomBar(product, onAddToCart)
            }
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F9FA))) {
            if (isWide) {
                // Wide Screen Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                        .widthIn(max = 1200.dp)
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
                        ProductImagePlaceholder(modifier = Modifier.fillMaxSize())
                    }
                    
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        ProductHeader(product)
                        Spacer(modifier = Modifier.height(16.dp))
                        ProductInfo(product)
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        if (sizeLabels.isNotEmpty()) {
                            SizeSelector(sizeLabels, selectedSize) { selectedSize = it }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        
                        if (colorLabels.isNotEmpty()) {
                            ColorSelector(colorLabels, selectedColor) { selectedColor = it }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                "$${product.price}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF5252)
                            )
                            Button(
                                onClick = { onAddToCart(product) },
                                modifier = Modifier.height(56.dp).weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ShoppingCart, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Add to Cart", fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(48.dp))
                        ReviewsSection()
                    }
                }
            } else {
                // Mobile Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    ProductImagePlaceholder(modifier = Modifier.fillMaxWidth().height(300.dp))
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
            IconButton(onClick = { /* Favorite */ }) {
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
                Text("Price", color = Color.Gray, fontSize = 14.sp)
                Text(
                    "$${product.price}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5252)
                )
            }
            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier
                    .height(52.dp)
                    .width(180.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Add to Cart", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProductImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE9EEF0)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Default.Image, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
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
            product.category.name.uppercase(),
            color = Color.Gray,
            style = MaterialTheme.typography.labelLarge,
            letterSpacing = 1.sp
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
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Black,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        "Description",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        product.description,
        style = MaterialTheme.typography.bodyLarge,
        color = Color.DarkGray,
        lineHeight = 24.sp
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
                    .size(width = 64.dp, height = 48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color(0xFFFF5252) else Color.White)
                    .border(
                        1.dp,
                        if (isSelected) Color(0xFFFF5252) else Color.LightGray,
                        RoundedCornerShape(12.dp),
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
                parseColor(colorStr)
            } catch (e: Exception) {
                Color.Gray
            }
            val isSelected = selectedColor == colorStr
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        if (isSelected) 3.dp else 0.dp,
                        Color.Black.copy(alpha = 0.5f),
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Customer Reviews",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No reviews yet for this product. Be the first to review!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }
    }
}

private fun parseColor(colorString: String): Color {
    if (colorString.startsWith("#")) {
        val color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) {
            return Color(color or 0x00000000FF000000L)
        } else if (colorString.length == 9) {
            return Color(color)
        }
    }
    return Color.Gray
}
