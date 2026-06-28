package com.track.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RegionConfig(
    val name: String,
    val active: Boolean,
    val slaHours: Int,
    val pricingTier: String
)

val defaultRegions = listOf(
    RegionConfig("Nairobi CBD", true, 2, "Standard"),
    RegionConfig("Nairobi Westlands", true, 2, "Standard"),
    RegionConfig("Mombasa", true, 6, "Premium"),
    RegionConfig("Eldoret", true, 8, "Premium"),
    RegionConfig("Kisumu", false, 10, "Economy"),
    RegionConfig("Nakuru", true, 6, "Standard"),
)

@Composable
fun RegionsConfigScreen(section: SuperAdminSection) {
    var regions by remember { mutableStateOf(defaultRegions) }
    var enableRealTimeTracking by remember { mutableStateOf(true) }
    var requirePhotoUpload by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            when (section) {
                SuperAdminSection.SERVICE_WINDOWS -> "Service Windows"
                SuperAdminSection.PRICING -> "Pricing & Fees"
                else -> "Regions & Routes"
            },
            fontWeight = FontWeight.Bold, fontSize = 18.sp
        )
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Regions list
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Active regions", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(12.dp))

                    // Header
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        listOf("Region" to 2f, "Active" to 0.8f, "SLA" to 0.8f, "Tier" to 1f)
                            .forEach { (col, w) ->
                                Text(col, modifier = Modifier.weight(w), fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                    }
                    HorizontalDivider()

                    regions.forEachIndexed { index, region ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(region.name, modifier = Modifier.weight(2f), fontSize = 13.sp)
                            Box(modifier = Modifier.weight(0.8f)) {
                                Switch(
                                    checked = region.active,
                                    onCheckedChange = { active ->
                                        regions = regions.toMutableList().also {
                                            it[index] = region.copy(active = active)
                                        }
                                    },
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                            Text("${region.slaHours}h", modifier = Modifier.weight(0.8f), fontSize = 12.sp)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (region.pricingTier) {
                                    "Premium" -> Color(0xFFE3F2FD)
                                    "Economy" -> Color(0xFFF5F5F5)
                                    else -> Color(0xFFF3E5F5)
                                },
                                modifier = Modifier.weight(1f).wrapContentHeight()
                            ) {
                                Text(
                                    region.pricingTier, fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }

            // Route builder + feature toggles
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Route builder
                Card(shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Route builder", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.height(12.dp))
                        var origin by remember { mutableStateOf("") }
                        var destination by remember { mutableStateOf("") }
                        var viaPoints by remember { mutableStateOf("") }

                        OutlinedTextField(value = origin, onValueChange = { origin = it },
                            label = { Text("Origin") }, modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.TripOrigin, null, Modifier.size(16.dp)) })
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = destination, onValueChange = { destination = it },
                            label = { Text("Destination") }, modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp)) })
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = viaPoints, onValueChange = { viaPoints = it },
                            label = { Text("Via points (comma-separated)") }, modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.AltRoute, null, Modifier.size(16.dp)) })
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                            Text("Save route")
                        }
                    }
                }

                // Feature toggles
                Card(shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Feature toggles", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Real-time tracking", fontSize = 13.sp)
                                Text("Enable live GPS updates", fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = enableRealTimeTracking, onCheckedChange = { enableRealTimeTracking = it })
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Photo upload on delivery", fontSize = 13.sp)
                                Text("Require courier proof-of-delivery photo", fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = requirePhotoUpload, onCheckedChange = { requirePhotoUpload = it })
                        }
                    }
                }
            }
        }
    }
}
