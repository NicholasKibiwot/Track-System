package com.track.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.track.domain.models.GeoLocation
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MockTrackMap(
    currentLocation: GeoLocation?,
    locationHistory: List<GeoLocation>,
    modifier: Modifier = Modifier,
) {
    // If no data, show waiting state
    if (currentLocation == null) {
        Card(
            modifier = modifier,
            colors =
                androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Waiting for GPS signal...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    // Normalize coordinates to fit the screen size
    // We take the min/max of all points to calculate the "zoom" level
    val allPoints = listOf(currentLocation) + locationHistory
    val minLat = allPoints.minOf { it.latitude }
    val maxLat = allPoints.maxOf { it.latitude }
    val minLon = allPoints.minOf { it.longitude }
    val maxLon = allPoints.maxOf { it.longitude }

    // Padding factor to keep markers away from edges
    val padding = 0.1f

    Box(modifier = modifier) {
        // 1. Draw Map Background (Grid)
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height

            // Draw Grid Lines
            val gridColor = Color.Gray.copy(alpha = 0.3f)
            val spacing = 50.dp.toPx()

            for (x in 0..width.toInt() step spacing.toInt()) {
                drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), height), strokeWidth = 2f)
            }
            for (y in 0..height.toInt() step spacing.toInt()) {
                drawLine(gridColor, Offset(0f, y.toFloat()), Offset(width, y.toFloat()), strokeWidth = 2f)
            }

            // 2. Draw Path (Polyline)
            if (locationHistory.isNotEmpty()) {
                val path = Path()
                val pointsToDraw = locationHistory + currentLocation

                pointsToDraw.forEachIndexed { index, loc ->
                    // Normalize Lat/Lon to X/Y
                    val normLat = ((loc.latitude - minLat) / (maxLat - minLat + 0.0001)).toFloat() // Avoid div by zero
                    val normLon = ((loc.longitude - minLon) / (maxLon - minLon + 0.0001)).toFloat()

                    // Map to screen coordinates (invert Y because screen Y goes down)
                    val x = width * (normLon * (1 - padding) + padding / 2)
                    val y = height * (1 - (normLat * (1 - padding) + padding / 2)) // Invert Y

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                drawPath(
                    path,
                    color = Color.Blue,
                    style = Stroke(width = 8f, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                )
            }

            // 3. Draw Current Location Marker
            val normLat = ((currentLocation.latitude - minLat) / (maxLat - minLat + 0.0001)).toFloat()
            val normLon = ((currentLocation.longitude - minLon) / (maxLon - minLon + 0.0001)).toFloat()
            val x = width * (normLon * (1 - padding) + padding / 2)
            val y = height * (1 - (normLat * (1 - padding) + padding / 2))

            // Blue Dot
            drawCircle(
                color = Color.Blue,
                radius = 20f,
                center = Offset(x, y),
            )
            // White Center
            drawCircle(
                color = Color.White,
                radius = 8f,
                center = Offset(x, y),
            )
        }

        // 4. Overlay Address Text
        Column(
            modifier =
                Modifier
                    .align(androidx.compose.ui.Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        androidx.compose.foundation.shape
                            .RoundedCornerShape(8.dp),
                    ).padding(8.dp),
        ) {
            Text(
                text = "📍 ${currentLocation.address}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
