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

@Composable
fun SystemHealthScreen(section: SuperAdminSection) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            if (section == SuperAdminSection.ERROR_LOGS) "Error Logs" else "System Health",
            fontWeight = FontWeight.Bold, fontSize = 18.sp
        )
        Spacer(Modifier.height(16.dp))

        if (section == SuperAdminSection.SYSTEM_HEALTH) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Server status
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Ktor server", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.height(12.dp))
                        HealthMetricRow("Uptime", "99.8%", isGood = true)
                        HealthMetricRow("Request volume (24h)", "14,302", isGood = true)
                        HealthMetricRow("Error rate", "0.2%", isGood = true)
                        HealthMetricRow("Avg response time", "142 ms", isGood = true)
                        HealthMetricRow("DB connections", "12 / 50", isGood = true)
                    }
                }

                // Client status
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Client platforms", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.height(12.dp))
                        HealthMetricRow("Android app", "v2.4.1 · 1,230 sessions", isGood = true)
                        HealthMetricRow("Web client", "Online · 340 sessions", isGood = true)
                        HealthMetricRow("Desktop app", "v1.1.0 · 42 sessions", isGood = true)
                        HealthMetricRow("iOS app", "v2.4.1 · 210 sessions", isGood = true)
                    }
                }

                // Integration health
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Integrations", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.height(12.dp))
                        HealthMetricRow("Firebase Firestore", "Connected", isGood = true)
                        HealthMetricRow("Firebase Auth", "Connected", isGood = true)
                        HealthMetricRow("FCM (push notifications)", "Connected", isGood = true)
                        HealthMetricRow("SMS gateway", "Degraded", isGood = false)
                        HealthMetricRow("Email provider", "Connected", isGood = true)
                    }
                }
            }
        } else {
            // Error logs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recent errors", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(12.dp))

                    val sampleLogs = listOf(
                        Triple("ERROR", "2026-06-28 17:30:12", "FirebaseException: PERMISSION_DENIED on orders/D-2041"),
                        Triple("WARN", "2026-06-28 17:15:44", "Slow query: getOrdersFlow() took 3200ms"),
                        Triple("ERROR", "2026-06-28 16:55:01", "SMS delivery failed for order #D-2033"),
                        Triple("INFO", "2026-06-28 16:40:22", "Courier #C-08 went offline while assigned to D-2038"),
                        Triple("WARN", "2026-06-28 16:12:55", "Stock level < 5 for SKU-NRB-009"),
                        Triple("ERROR", "2026-06-28 15:44:10", "NullPointerException in DispatchController.assignCourier()"),
                    )

                    sampleLogs.forEach { (level, time, message) ->
                        val (bg, fg) = when (level) {
                            "ERROR" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                            "WARN" -> Color(0xFFFFF8E1) to Color(0xFFE65100)
                            else -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = RoundedCornerShape(3.dp), color = bg) {
                                Text(level, color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(time, fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(140.dp))
                            Text(message, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun HealthMetricRow(label: String, value: String, isGood: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isGood) Icons.Default.CheckCircle else Icons.Default.Warning,
                null,
                tint = if (isGood) Color(0xFF4CAF50) else Color(0xFFFF9800),
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                color = if (isGood) MaterialTheme.colorScheme.onSurface else Color(0xFFE65100))
        }
    }
}
