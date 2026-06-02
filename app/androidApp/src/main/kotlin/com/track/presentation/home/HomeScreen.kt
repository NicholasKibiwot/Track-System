package com.track.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.track.domain.models.Product
import com.track.domain.models.User
import com.track.domain.models.UserRole
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.customer.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCart: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToMyOrders: () -> Unit,
    viewModel: CustomerViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cartCount = viewModel.cartCount
    val currentUser by authViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                currentUser = currentUser,
                cartCount = cartCount,
                onNavigateToCart = onNavigateToCart,
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToMyOrders = onNavigateToMyOrders
            )
        },
    ) { padding ->
        HomeContent(
            padding = padding,
            isLoading = isLoading,
            products = products,
            onAddToCart = { viewModel.addToCart(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    currentUser: User?,
    cartCount: Int,
    onNavigateToCart: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToMyOrders: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                "YheCutMedia Shop",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            if (currentUser != null && currentUser.role == UserRole.CUSTOMER) {
                IconButton(onClick = onNavigateToMyOrders) {
                    Icon(Icons.Default.Person, contentDescription = "My Orders")
                }
            }
            if (currentUser == null) {
                IconButton(onClick = onNavigateToLogin) {
                    Icon(Icons.Default.Person, contentDescription = "Login")
                }
            }
            IconButton(onClick = onNavigateToCart) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge { Text("$cartCount") }
                        }
                    },
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                }
            }
        },
    )
}

@Composable
private fun HomeContent(
    padding: PaddingValues,
    isLoading: Boolean,
    products: List<Product>,
    onAddToCart: (Product) -> Unit
) {
    when {
        isLoading -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        products.isEmpty() -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "No products available yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        else -> {
            LazyColumn(
                modifier =
                    Modifier
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = { onAddToCart(product) },
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "KES ${product.price}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text =
                            if (product.stock > 0) {
                                "${product.stock} in stock"
                            } else {
                                "Out of stock"
                            },
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
}
