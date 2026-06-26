package com.track.presentation.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.util.kmpViewModel
import com.track.domain.models.Product
import com.track.presentation.customer.CustomerViewModel
import com.track.util.isWideScreen

@Composable
fun ModernHomeScreen(
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
    viewModel: CustomerViewModel = kmpViewModel<CustomerViewModel>(),
) {
    val products by viewModel.products.collectAsState()
    val isWide = isWideScreen()
    val scrollState = rememberScrollState()

    val categories = listOf(
        ModernCategory("Industrial Printers", Icons.Default.Print, Color(0xFFE3F2FD)),
        ModernCategory("3D Printers", Icons.Default.ViewInAr, Color(0xFFF3E5F5)),
        ModernCategory("Spare Parts", Icons.Default.Settings, Color(0xFFE8F5E9)),
        ModernCategory("Ink & Toner", Icons.Default.Palette, Color(0xFFFFF3E0)),
        ModernCategory("Accessories", Icons.Default.Cable, Color(0xFFFFEBEE)),
    )

    Scaffold(
        topBar = { ModernTopBar(onNavigateToCart, onNavigateToProfile, isWide) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF4F4F4))
                .verticalScroll(scrollState)
        ) {
            // Hero Section (Amazon style)
            ModernHeroBanner(isWide)

            Column(
                modifier = Modifier
                    .widthIn(max = 1400.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = if (isWide) 24.dp else 12.dp)
            ) {
                // Category Strips
                ModernCategoryStrip(categories)

                Spacer(modifier = Modifier.height(24.dp))

                // Feature Grid (Amazon/eBay style cards)
                ModernFeatureGrid(products, onNavigateToProductDetails, isWide)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Recommendation Section
                ModernProductRow("Trending in 3D Printing", products.filter { it.category.contains("3D", true) }, onNavigateToProductDetails)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                ModernProductRow("Essential Spare Parts", products.filter { it.category.contains("Part", true) }, onNavigateToProductDetails)

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopBar(onCart: () -> Unit, onProfile: () -> Unit, isWide: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF131921), // Amazon dark navy
        shadowElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand
                Text(
                    "PRINTER PRO",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(end = 24.dp)
                )

                if (isWide) {
                    // Delivery Info
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 24.dp)) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Column {
                            Text("Deliver to", color = Color.LightGray, fontSize = 11.sp)
                            Text("Nairobi, KE", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Search Bar
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text("Search for printers, parts or tools...", color = Color.Gray, fontSize = 14.sp)
                    }
                    Surface(
                        color = Color(0xFFFEB123), // Amazon orange
                        modifier = Modifier.fillMaxHeight().width(50.dp),
                        onClick = {}
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Search, null, tint = Color(0xFF333333))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Actions
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Column(
                        modifier = Modifier.clickable { onProfile() },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Hello, Sign in", color = Color.White, fontSize = 11.sp)
                        Text("Account & Lists", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(modifier = Modifier.clickable { onCart() }) {
                        Icon(Icons.Outlined.ShoppingCart, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Surface(
                            color = Color(0xFFF08804),
                            shape = CircleShape,
                            modifier = Modifier.size(18.dp).align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("0", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            
            // Secondary Nav Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF232F3E)) // Lighter navy
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Menu, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Text(" All", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                listOf("Bulk Orders", "Parts Finder", "Customer Service", "Registry", "Sell").forEach {
                    Text(it, color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun ModernHeroBanner(isWide: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isWide) 400.dp else 220.dp)
    ) {
        // Gradient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE3E6E6), Color(0xFFF4F4F4))
                    )
                )
        )
        
        // Mock Image / Graphic
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            color = Color(0xFF004B3E) // Industrial green
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("INDUSTRIAL PRECISION", color = Color.White.copy(0.6f), letterSpacing = 4.sp)
                    Text("NEXT-GEN PRINTERS", color = Color.White, fontSize = if (isWide) 48.sp else 28.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEB123)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Explore Global Inventory", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ModernCategoryStrip(categories: List<ModernCategory>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-40).dp),
        shape = RoundedCornerShape(4.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        LazyRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(categories) { category ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = category.color,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(category.icon, null, modifier = Modifier.size(28.dp), tint = Color(0xFF333333))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(category.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun ModernFeatureGrid(products: List<Product>, onClick: (String) -> Unit, isWide: Boolean) {
    val columns = if (isWide) 4 else 1
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        for (i in 0 until (products.size.coerceAtMost(8) / columns)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (j in 0 until columns) {
                    val index = i * columns + j
                    if (index < products.size) {
                        AmazonStyleProductCard(
                            product = products[index],
                            modifier = Modifier.weight(1f),
                            onClick = { onClick(products[index].id) }
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun AmazonStyleProductCard(product: Product, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(380.dp),
        color = Color.White,
        shape = RoundedCornerShape(4.dp),
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFF8F8F8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Print, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                if (product.stock < 5) {
                    Surface(
                        color = Color(0xFFCC0C39),
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text("Limited Stock", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    Icon(
                        Icons.Default.Star, 
                        null, 
                        tint = if (i < product.rating.toInt()) Color(0xFFFFA41C) else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(" ${product.rating}", fontSize = 12.sp, color = Color(0xFF007185))
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row {
                Text("$", fontSize = 14.sp, modifier = Modifier.offset(y = 4.dp))
                Text("${product.price.toInt()}", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(".${((product.price % 1) * 100).toInt()}", fontSize = 14.sp, modifier = Modifier.offset(y = 4.dp))
            }
            
            Text("FREE delivery by PRINTER PRO", color = Color(0xFF565959), fontSize = 13.sp)
        }
    }
}

@Composable
fun ModernProductRow(title: String, products: List<Product>, onClick: (String) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text("Shop all", color = Color(0xFF007185), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
        }
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products) { product ->
                Surface(
                    modifier = Modifier.size(width = 160.dp, height = 220.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    onClick = { onClick(product.id) }
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFFF8F8F8)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Settings, null, tint = Color.LightGray)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(product.name, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text("$${product.price}", fontWeight = FontWeight.Bold, color = Color(0xFFB12704))
                    }
                }
            }
        }
    }
}

data class ModernCategory(val name: String, val icon: ImageVector, val color: Color)
