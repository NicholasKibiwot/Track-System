package com.track.presentation.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.track.util.kmpViewModel
import com.track.data.FakeData
import com.track.models.Product
import com.track.models.Category
import com.track.presentation.customer.CustomerViewModel

@Composable
fun CustomerDashboard(
    viewModel: CustomerViewModel = kmpViewModel(),
    onProductClick: (Product) -> Unit = {},
    onCategoryClick: (Category) -> Unit = {},
) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("YheCutMedia Shop") })
        },
    ) { padding ->
        when {
            isLoading && products.isEmpty() && categories.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Categories",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(categories) { category ->
                                CategoryCard(
                                    category = category,
                                    onClick = { onCategoryClick(category) }
                                )
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                "Recommended for You",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    items(products) { product: Product ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            DashboardProductCard(
                                product = product,
                                onAddToCart = { onProductClick(product) },
                            )
                        }
                    }
                    
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    val icon = when (category.id) {
        "OFFICE_PRINTERS" -> Icons.Default.Print
        "POS_RETAIL_PRINTERS" -> Icons.Default.ShoppingCart
        "COMMERCIAL_GRAPHICS_PRINTERS" -> Icons.Default.Category
        "INDUSTRIAL_SPECIALTY_PRINTERS" -> Icons.Default.Settings
        "REPAIRS_SERVICES" -> Icons.Default.Build
        else -> Icons.Default.Category
    }

    Surface(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun DashboardProductCard(
    product: Product,
    onAddToCart: () -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Product Image
            val displayImageUrl = product.images.firstOrNull()?.storage?.webpUrl 
                ?: product.images.firstOrNull()?.storage?.webpPath // fallback to path if URL not resolved
                ?: product.imageUrl

            SubcomposeAsyncImage(
                model = displayImageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                },
                error = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Image, null, tint = Color.LightGray)
                    }
                }
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "KES ${product.price}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    "Stock: ${product.stock}",
                    style = MaterialTheme.typography.labelSmall,
                    color =
                        if (product.stock > 0) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                )
            }
            Button(
                onClick = onAddToCart,
                enabled = product.stock > 0,
            ) {
                Text(if (product.stock > 0) "Add to Cart" else "Out of Stock")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerDashboardPreview() {
    Column(Modifier.padding(16.dp)) {
        FakeData.previewProducts.forEach { product ->
            DashboardProductCard(product = product)
            Spacer(Modifier.height(8.dp))
        }
    }
}

