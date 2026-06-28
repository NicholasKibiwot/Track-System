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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.track.models.GeoLocation

@Composable
fun MockTrackMap(
    currentLocation: GeoLocation?,
    locationHistory: List<GeoLocation>,
    modifier: Modifier = Modifier,
) {
    if (currentLocation == null) {
        WaitingState(modifier)
        return
    }

    val allPoints = listOf(currentLocation) + locationHistory
    val bounds = MapBounds.calculate(allPoints)
    val padding = 0.1f

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawGrid()
            drawUserPath(locationHistory, currentLocation, bounds, padding)
            drawCurrentMarker(currentLocation, bounds, padding)
        }

        AddressOverlay(currentLocation.address, Modifier.align(androidx.compose.ui.Alignment.BottomCenter))
    }
}

@Composable
private fun WaitingState(modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Waiting for GPS signal...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun DrawScope.drawGrid() {
    val gridColor = Color.Gray.copy(alpha = 0.3f)
    val spacing = 50.dp.toPx()
    for (x in 0..size.width.toInt() step spacing.toInt()) {
        drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 2f)
    }
    for (y in 0..size.height.toInt() step spacing.toInt()) {
        drawLine(gridColor, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 2f)
    }
}

private fun DrawScope.drawUserPath(
    history: List<GeoLocation>,
    current: GeoLocation,
    bounds: MapBounds,
    padding: Float
) {
    if (history.isEmpty()) return
    val path = Path()
    val points = history + current
    points.forEachIndexed { index, loc ->
        val pos = bounds.normalize(loc, size, padding)
        if (index == 0) path.moveTo(pos.x, pos.y) else path.lineTo(pos.x, pos.y)
    }
    drawPath(path, Color.Blue, style = Stroke(width = 8f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
}

private fun DrawScope.drawCurrentMarker(loc: GeoLocation, bounds: MapBounds, padding: Float) {
    val pos = bounds.normalize(loc, size, padding)
    drawCircle(Color.Blue, radius = 20f, center = pos)
    drawCircle(Color.White, radius = 8f, center = pos)
}

@Composable
private fun AddressOverlay(address: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            ).padding(8.dp),
    ) {
        Text(
            text = "📍 $address",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private data class MapBounds(val minLat: Double, val maxLat: Double, val minLon: Double, val maxLon: Double) {
    companion object {
        fun calculate(points: List<GeoLocation>) = MapBounds(
            points.minOf { it.latitude }, points.maxOf { it.latitude },
            points.minOf { it.longitude }, points.maxOf { it.longitude }
        )
    }
    fun normalize(loc: GeoLocation, size: Size, padding: Float): Offset {
        val normLat = ((loc.latitude - minLat) / (maxLat - minLat + 0.0001)).toFloat()
        val normLon = ((loc.longitude - minLon) / (maxLon - minLon + 0.0001)).toFloat()
        return Offset(
            size.width * (normLon * (1 - padding) + padding / 2),
            size.height * (1 - (normLat * (1 - padding) + padding / 2))
        )
    }
}
