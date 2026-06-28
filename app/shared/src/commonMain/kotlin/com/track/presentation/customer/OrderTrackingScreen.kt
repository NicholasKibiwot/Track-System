package com.track.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.models.Order
import com.track.models.OrderStatus
import com.track.models.TrackingRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String,
    viewModel: CustomerViewModel,
    onBackClick: () -> Unit
) {
    val order = viewModel.getOrderById(orderId)
    val trackingRecord by viewModel.trackingRecord.collectAsState()
    
    LaunchedEffect(orderId) {
        viewModel.watchTrackingRecord(orderId)
    }

    Scaffold(
        topBar = {
            TrackingTopAppBar(onBackClick)
        }
    ) { padding ->
        if (order == null) {
            LoadingBox()
        } else {
            OrderTrackingContent(
                padding = padding,
                order = order,
                trackingRecord = trackingRecord
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackingTopAppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Track Order", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { /* More options */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
        }
    )
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun OrderTrackingContent(
    padding: PaddingValues,
    order: Order,
    trackingRecord: TrackingRecord?
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MapViewPlaceholder(trackingRecord)

        Column(modifier = Modifier.padding(20.dp)) {
            OrderTrackingHeader(order)

            Spacer(modifier = Modifier.height(24.dp))

            // Courier Info (if assigned)
            order.driverName?.let { driverName ->
                CourierCard(driverName, "YheCutMedia Logistics")
                Spacer(modifier = Modifier.height(24.dp))
            }

            JourneyTimelineSection(order.orderStatus)
        }
    }
}

@Composable
private fun MapViewPlaceholder(trackingRecord: TrackingRecord?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFE3F2FD)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2196F3), modifier = Modifier.size(48.dp))
            Text("Live Map View", fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
            trackingRecord?.currentLocation?.let {
                Text("${it.lat}, ${it.lng}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun OrderTrackingHeader(order: Order) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Delivery Status", color = Color.Gray, fontSize = 14.sp)
            Text(
                text = order.orderStatus.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Surface(
            color = Color(0xFFF1F3F4),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = order.trackingNumber,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun JourneyTimelineSection(status: OrderStatus) {
    Text("Order Journey", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(16.dp))
    
    TrackingTimeline(
        steps = listOf(
            TimelineStep("Order Placed", "Your order has been received", OrderStatus.PENDING),
            TimelineStep("Processing", "Items are being prepared at the branch", OrderStatus.PROCESSING),
            TimelineStep("In Transit", "Driver is on the way to your destination", OrderStatus.INTRANSIT),
            TimelineStep("Delivered", "Package has been handed over", OrderStatus.DELIVERED)
        ),
        currentStatus = status
    )
}

@Composable
fun CourierCard(name: String, company: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF8F9FA),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalShipping, null, tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(company, fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(
                onClick = { /* Call driver */ },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.Call, null, tint = Color.White)
            }
        }
    }
}

data class TimelineStep(val title: String, val subtitle: String, val status: OrderStatus)

@Composable
fun TrackingTimeline(steps: List<TimelineStep>, currentStatus: OrderStatus) {
    val currentIndex = steps.indexOfFirst { it.status == currentStatus }.let { if (it == -1) 0 else it }

    Column {
        steps.forEachIndexed { index, step ->
            val isCompleted = index <= currentIndex
            val isLast = index == steps.size - 1

            TimelineItem(step, isCompleted, isLast)
        }
    }
}

@Composable
private fun TimelineItem(step: TimelineStep, isCompleted: Boolean, isLast: Boolean) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        TimelineIndicator(isCompleted, isLast)
        
        Column(modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)) {
            Text(
                text = step.title,
                fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                color = if (isCompleted) Color.Black else Color.Gray
            )
            Text(
                text = step.subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun TimelineIndicator(isCompleted: Boolean, isLast: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isCompleted) MaterialTheme.colorScheme.primary else Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = Color.White)
            }
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(if (isCompleted) MaterialTheme.colorScheme.primary else Color.LightGray)
            )
        }
    }
}
