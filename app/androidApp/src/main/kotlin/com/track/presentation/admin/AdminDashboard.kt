package com.track.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel // 👈 CRITICAL IMPORT
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.track.domain.models.Order
import com.track.domain.models.OrderStatus
import com.track.ui.theme.Gray200
import com.track.ui.theme.Gray400
import com.track.ui.theme.StatusDelayed
import com.track.ui.theme.StatusDelivered
import com.track.ui.theme.StatusInTransit
import com.track.ui.theme.StatusPending


@Composable
fun AdminDashboard(
    viewModel: AdminViewModel = hiltViewModel(),
    onOrderClick: (String) -> Unit = {}
) {
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showTutorial by remember { mutableStateOf(true) }
    var tutorialStep by remember { mutableIntStateOf(0) }

    val inTransitCount = orders.count { it.orderStatus == OrderStatus.INTRANSIT }
    val deliveredCount = orders.count { it.orderStatus == OrderStatus.DELIVERED }
    val pendingCount = orders.count { it.orderStatus == OrderStatus.PENDING }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YheCutMedia Admin") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            AdminDashboardLoading(paddingValues)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("In Transit", inTransitCount.toString(), StatusInTransit)
                    StatCard("Delivered", deliveredCount.toString(), StatusDelivered)
                    StatCard("Pending", pendingCount.toString(), StatusPending)
                }

                Text("Office Status", style = MaterialTheme.typography.titleMedium)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OfficeStatusRow("Eldoret HQ", "${orders.count { it.origin == "Eldoret HQ" && it.orderStatus != OrderStatus.DELIVERED }} machines active", StatusInTransit)
                        OfficeStatusRow("Nakuru Branch", "${orders.count { it.origin == "Nakuru Branch" && it.orderStatus != OrderStatus.DELIVERED }} machines active", StatusPending)
                        OfficeStatusRow("Nairobi Hub", "${orders.count { it.destination == "Nairobi Hub" }} shipments received", StatusDelivered)
                    }
                }

                Text("Recent Shipments", style = MaterialTheme.typography.titleMedium)
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(orders) { order ->
                        ShipmentCard(order, onClick = { onOrderClick(order.id) })
                    }
                }
            }
        }
    }

    if (showTutorial && !isLoading) {
        TutorialOverlay(
            currentStep = tutorialStep,
            onNext = { if (tutorialStep < 2) tutorialStep++ else showTutorial = false },
            onSkip = { showTutorial = false },
            onFinish = { showTutorial = false }
        )
    }
}
@Composable
private fun RowScope.StatCard(label: String, value: String, color: Color) {
    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun OfficeStatusRow(name: String, status: String, indicatorColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(indicatorColor))
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, style = MaterialTheme.typography.bodyMedium)
        }
        Text(status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun ShipmentCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(
                    when (order.orderStatus) {
                        OrderStatus.INTRANSIT -> StatusInTransit
                        OrderStatus.DELIVERED -> StatusDelivered
                        OrderStatus.PENDING -> StatusPending
                        OrderStatus.DELAYED -> StatusDelayed
                        else -> Gray400
                    }
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val itemText = order.items.firstOrNull()?.let { "${it.productName} (${it.machineType?.displayName})" } ?: "Unknown Item"
                Text(itemText, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text("${order.origin} → ${order.destination}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Text(order.orderStatus.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AdminDashboardLoading(paddingValues: PaddingValues) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(3) { Box(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(12.dp)).background(Gray200)) }
        Box(modifier = Modifier.height(24.dp).width(120.dp).clip(RoundedCornerShape(4.dp)).background(Gray200))
        Box(modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).background(Gray200))
        Box(modifier = Modifier.height(24.dp).width(120.dp).clip(RoundedCornerShape(4.dp)).background(Gray200))
        repeat(2) { Box(modifier = Modifier.fillMaxWidth().height(70.dp).clip(RoundedCornerShape(12.dp)).background(Gray200)) }
    }
}

@Composable
private fun TutorialOverlay(currentStep: Int, onNext: () -> Unit, onSkip: () -> Unit, onFinish: () -> Unit) {
    val steps = listOf("👋 Welcome to YheCutMedia Admin!", "📦 Monitor real-time status.", "🔮 Coming Soon: Live GPS.")
    val titles = listOf("Welcome!", "Live Status", "What's Next")
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f))) {
        Card(modifier = Modifier.fillMaxWidth().padding(24.dp).align(Alignment.Center), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(if (index <= currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(titles[currentStep], style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(steps[currentStep], style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onSkip) { Text("Skip", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
                    Button(onClick = if (currentStep == 2) onFinish else onNext) {
                        Text(if (currentStep == 2) "Got It" else "Next", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}