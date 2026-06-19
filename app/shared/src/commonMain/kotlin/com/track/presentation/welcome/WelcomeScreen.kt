package com.track.presentation.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.util.isWideScreen

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    val isWide = isWideScreen()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (isWide) {
            // Desktop Layout
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .background(Color(0xFFFF5252)),
                    contentAlignment = Alignment.Center
                ) {
                    WelcomeIllustration()
                }
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(64.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    WelcomeText(textAlign = TextAlign.Start)
                    Spacer(modifier = Modifier.height(48.dp))
                    WelcomeButtons(onGetStarted, onSignIn, fullWidth = false)
                }
            }
        } else {
            // Mobile Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    WelcomeIllustration()
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                WelcomeText(textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(48.dp))
                WelcomeButtons(onGetStarted, onSignIn, fullWidth = true)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun WelcomeIllustration() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false,
        modifier = Modifier.widthIn(max = 400.dp)
    ) {
        items(9) { index ->
            Box(
                modifier = Modifier
                    .aspectRatio(0.8f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5).copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Text("Product $index", color = Color.Gray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun WelcomeText(textAlign: TextAlign) {
    Column(horizontalAlignment = if (textAlign == TextAlign.Center) Alignment.CenterHorizontally else Alignment.Start) {
        Text(
            text = "Your Premier Destination\nfor Track & Shop",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                lineHeight = 44.sp,
                fontSize = 36.sp
            ),
            textAlign = textAlign,
            color = Color(0xFF1A1C1E)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Discover the latest tech, fashion, and lifestyle products delivered right to your doorstep with real-time tracking.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = textAlign,
            color = Color.Gray,
            modifier = Modifier.widthIn(max = 500.dp)
        )
    }
}

@Composable
private fun WelcomeButtons(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit,
    fullWidth: Boolean
) {
    Column(
        modifier = if (fullWidth) Modifier.fillMaxWidth() else Modifier.width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Explore Products", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account? ", color = Color.Gray)
            TextButton(
                onClick = onSignIn,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Sign In",
                    color = Color(0xFFFF5252),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
