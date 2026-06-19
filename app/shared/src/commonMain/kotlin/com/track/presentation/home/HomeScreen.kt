package com.track.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.util.kmpViewModel
import com.track.domain.models.Product
import com.track.presentation.customer.CustomerViewModel
import com.track.util.isWideScreen

private const val SEE_ALL = "See All"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
    viewModel: CustomerViewModel = kmpViewModel(),
) {
    val products by viewModel.products.collectAsState()
    val isWide = isWideScreen()
    
    val categories = listOf(
        CategoryItem("Clothes", Icons.Default.Checkroom, Color(0xFFFFEBEE)),
        CategoryItem("Electronics", Icons.Default.Devices, Color(0xFFE3F2FD)),
        CategoryItem("Shoes", Icons.AutoMirrored.Filled.DirectionsRun, Color(0xFFE8F5E9)),
        CategoryItem("Watch", Icons.Default.Watch, Color(0xFFFFF3E0)),
    )

    Row(modifier = Modifier.fillMaxSize()) {
        if (isWide) {
            // Desktop Side Navigation
            NavigationRail(
                containerColor = Color(0xFFF8F9FA),
                header = {
                    Icon(
                        Icons.Default.TrackChanges,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(32.dp).padding(vertical = 16.dp)
                    )
                }
            ) {
                NavigationRailItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    selected = true,
                    onClick = {}
                )
                NavigationRailItem(
                    icon = { Icon(Icons.Default.ShoppingCart, null) },
                    label = { Text("Cart") },
                    selected = false,
                    onClick = onNavigateToCart
                )
                NavigationRailItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = onNavigateToProfile
                )
            }
        }

        Scaffold(
            bottomBar = {
                if (!isWide) {
                    NavigationBar(containerColor = Color.White) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, null) },
                            label = { Text("Home") },
                            selected = true,
                            onClick = { }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.ShoppingCart, null) },
                            label = { Text("Cart") },
                            selected = false,
                            onClick = onNavigateToCart
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, null) },
                            label = { Text("Profile") },
                            selected = false,
                            onClick = onNavigateToProfile
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF8F9FA))
            ) {
                HomeHeader(isWide)

                Box(modifier = Modifier.fillMaxWidth().padding(if (isWide) 32.dp else 16.dp)) {
                    Column(
                        modifier = Modifier.widthIn(max = 1200.dp).align(Alignment.TopCenter)
                    ) {
                        // Banners
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            BannerCard(modifier = Modifier.weight(1f))
                            if (isWide) {
                                BannerCard(modifier = Modifier.weight(1f), color = Color(0xFF1A1C1E), title = "New Arrivals\nUp to 20% Off")
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Categories
                        SectionHeader(title = "Categories")
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(if (isWide) 32.dp else 16.dp)
                        ) {
                            categories.forEach { category ->
                                CategoryIcon(category, isWide)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Products Grid
                        SectionHeader(title = "Featured Products")
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val columns = if (isWide) 4 else 2
                        products.chunked(columns).forEach { rowProducts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                rowProducts.forEach { product ->
                                    ProductCard(
                                        product = product,
                                        modifier = Modifier.weight(1f),
                                    ) { onNavigateToProductDetails(product.id) }
                                }
                                // Fill remaining space if row is not full
                                repeat(columns - rowProducts.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeHeader(isWide: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFF5252), RoundedCornerShape(bottomStart = if (isWide) 0.dp else 24.dp, bottomEnd = if (isWide) 0.dp else 24.dp))
            .padding(if (isWide) 24.dp else 16.dp)
    ) {
        Column(modifier = Modifier.widthIn(max = 1200.dp).align(Alignment.Center)) {
            if (!isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Location", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text(" Nairobi, Kenya", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Notifications, null, tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isWide) {
                    Text(
                        "Track Shop",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 32.dp)
                    )
                }
                
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search products, brands and categories") },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                
                if (isWide) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Search")
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        TextButton(onClick = {}) {
            Text(SEE_ALL, color = Color(0xFFFF5252))
        }
    }
}

@Composable
fun BannerCard(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    title: String = "Special Offer\nUp to 40% Off"
) {
    Box(
        modifier = modifier
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Limited time!", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            Text(
                title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Shop Now")
            }
        }
    }
}

@Composable
fun CategoryIcon(category: CategoryItem, isWide: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(if (isWide) 80.dp else 60.dp)
                .clip(CircleShape)
                .background(category.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                category.icon,
                null,
                tint = Color(0xFFFF5252),
                modifier = Modifier.size(if (isWide) 32.dp else 24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(category.name, fontSize = if (isWide) 14.sp else 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFF1F3F4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Image, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 16.sp)
                Text(product.category, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$${product.price}", fontWeight = FontWeight.Black, color = Color(0xFFFF5252), fontSize = 18.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Text(" ${product.rating}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class CategoryItem(
    val name: String,
    val icon: ImageVector,
    val backgroundColor: Color
)
