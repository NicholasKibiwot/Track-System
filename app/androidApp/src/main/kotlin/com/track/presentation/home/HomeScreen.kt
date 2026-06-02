package com.track.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.track.domain.models.Product
import com.track.presentation.customer.CustomerViewModel

@Composable
fun HomeScreen(
    onNavigateToCart: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: CustomerViewModel = hiltViewModel(),
) {
    val products by viewModel.products.collectAsState()
    val cartCount by remember { derivedStateOf { viewModel.cartCount } }

    Scaffold(
        bottomBar = {
            HomeBottomNavigation(
                cartCount = cartCount,
                onCartClick = onNavigateToCart,
                onAccountClick = onNavigateToLogin
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            HomeHeader()

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    HomeHeroBanner()
                }

                item(span = { GridItemSpan(2) }) {
                    CategoryTabs()
                }

                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Premium Printers",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Elite performance for professionals",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        TextButton(onClick = {}) {
                            Text("View all", color = Color.Black)
                        }
                    }
                }

                items(products) { product ->
                    StoreProductCard(
                        product = product,
                        onProductClick = { onNavigateToProduct(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun HomeHeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
    ) {
        // Text Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                "Ultimate Printing",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Check", modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun CategoryTabs() {
    val categories = listOf("Industrial", "Office", "Home", "Accessories", "3D Printers")
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            Surface(
                modifier = Modifier.clickable { },
                shape = RoundedCornerShape(24.dp),
                color = if (category == "Industrial") Color.Black else Color(0xFFF5F5F5)
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    color = if (category == "Industrial") Color.White else Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun StoreProductCard(product: Product, onProductClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.85f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF9F9F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            
            // Star Rating
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFBA49), modifier = Modifier.size(14.dp))
                }
                Text(" (5)", fontSize = 10.sp, color = Color.Gray)
            }
            
            // Bag/Cart Shortcut
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(y = 12.dp)
                    .size(36.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            product.category,
            fontSize = 11.sp,
            color = Color.Gray
        )
        
        Text(
            product.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            "KES ${product.price}",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun HomeBottomNavigation(
    cartCount: Int,
    onCartClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Shop") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onCartClick,
            icon = {
                BadgedBox(badge = { if (cartCount > 0) Badge { Text(cartCount.toString()) } }) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null)
                }
            },
            label = { Text("Bag") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null) },
            label = { Text("Favorites") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onAccountClick,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") }
        )
    }
}
