package com.track

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource

import track.app.shared.generated.resources.Res
import track.app.shared.generated.resources.compose_multiplatform

import com.track.presentation.navigation.AppNavHost
import com.track.ui.theme.AppTheme
import com.track.util.kmpViewModel

@Composable
@Preview
fun App() {
    AppTheme {
        AppNavHost(
            authViewModel = kmpViewModel(),
            customerViewModel = kmpViewModel(),
            adminViewModel = kmpViewModel()
        )
    }
}
