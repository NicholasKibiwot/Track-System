package com.track.presentation.customer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.graphics.vector.ImageVector
import com.track.models.Product
import com.track.presentation.customer.CustomerViewModel
import com.track.util.kmpViewModel

val ShopPrimary = Color(0xFFFF1B82)
val ShopBackground = Color(0xFFFFF0F5)
val ShopSecondary = Color(0xFFFF9800)

@Composable
fun ShopHomeScreen(
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToOrders: () -> Unit,
    viewModel: CustomerViewModel = kmpViewModel<CustomerViewModel>()
) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val cartCount by viewModel.cartItems.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            ShopBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { 
                    selectedTab = it 
                    when(it) {
                        0 -> {} // Home
                        1 -> {} // Analytics/Categories
                        2 -> onNavigateToCart() 
                        3 -> onNavigateToOrders() 
                        4 -> onNavigateToProfile() 
                    }
                },
                cartCount = cartCount.sumOf { it.quantity }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShopBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ShopTopBar()
            
            WelcomeSection()
            
            ShopSearchBar()
            
            CategorySection(onNavigateToCategory)
            
            PromoBanners()
            
            ProductSection(products, onNavigateToProductDetails, viewModel)
            
            Spacer(Modifier.height(100.dp)) // Padding for bottom nav
        }
    }
}

@Composable
fun ShopTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Menu, contentDescription = null, tint = Color.Black)
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(8.dp),
                color = ShopPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("T", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Track Shop", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Creative Commerce", fontSize = 10.sp, color = Color.Gray)
            }
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            BadgedBox(badge = { Badge { Text("9+") } }) {
                Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun WelcomeSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color.LightGray
            ) {
                Icon(Icons.Default.Person, null, tint = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Welcome,", color = ShopPrimary, fontSize = 12.sp)
                Text("User", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF311B92))
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Nairobi", color = Color.Gray, fontSize = 14.sp)
            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Gray)
            Icon(Icons.Default.LocationOn, null, tint = ShopPrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun ShopSearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Search products...", color = Color.Gray, modifier = Modifier.weight(1f))
                Icon(Icons.Default.Search, null, tint = ShopPrimary)
            }
        }
        Spacer(Modifier.width(12.dp))
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = ShopPrimary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun CategorySection(onCategoryClick: (String) -> Unit) {
    val categories = listOf(
        Pair("Vit Rich", Icons.Default.Eco),
        Pair("Strength", Icons.Default.FitnessCenter),
        Pair("Wattery", Icons.Default.WaterDrop),
        Pair("Seasonal", Icons.Default.WbSunny),
        Pair("Citric", Icons.Default.Agriculture),
        Pair("Spa", Icons.Default.Spa)
    )
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            val (name, icon) = category
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onCategoryClick(name) }
            ) {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = Color(0xFFD84315), modifier = Modifier.size(30.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(name, fontSize = 12.sp, color = ShopPrimary)
            }
        }
    }
}

@Composable
fun PromoBanners() {
    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { PromoCard(ShopPrimary) }
        item { PromoCard(ShopSecondary) }
    }
}

@Composable
fun PromoCard(color: Color) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        color = color
    ) {
        Box {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("10% OFF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text("On first order", color = Color.White, fontSize = 16.sp)
                
                Spacer(Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) {
                        Surface(
                            modifier = Modifier.size(24.dp).offset(x = (it * -8).dp),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color.White),
                            color = Color.LightGray
                        ) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = Color.White)
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Trusted by thousands", color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ProductSection(products: List<Product>, onProductClick: (String) -> Unit, viewModel: CustomerViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Products", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("View All", color = ShopPrimary, fontSize = 14.sp)
        }
        
        Spacer(Modifier.height(16.dp))
        
        val chunkedProducts = products.chunked(2)
        chunkedProducts.forEach { rowProducts ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowProducts.forEach { product ->
                    ShopProductCard(
                        product = product,
                        modifier = Modifier.weight(1f),
                        onClick = { onProductClick(product.id) },
                        onAddToCart = { viewModel.addToCart(product) }
                    )
                }
                if (rowProducts.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ShopProductCard(
    product: Product,
    modifier: Modifier,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column {
            Box(modifier = Modifier.height(140.dp).fillMaxWidth()) {
                val imageUrl = product.images.firstOrNull()?.storage?.webpUrl ?: product.imageUrl
                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = ShopPrimary
                ) {
                    Text("10% Off", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF311B92))
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("KES ${product.price}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                        Text("1 unit", fontSize = 11.sp, color = Color.Gray)
                    }
                    
                    Surface(
                        modifier = Modifier.size(32.dp).clickable { onAddToCart() },
                        shape = CircleShape,
                        color = ShopPrimary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
