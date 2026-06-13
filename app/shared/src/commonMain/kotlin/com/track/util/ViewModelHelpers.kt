package com.track.util

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

@Composable
expect inline fun <reified T : ViewModel> kmpViewModel(): T

