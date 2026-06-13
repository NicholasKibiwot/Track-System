package com.track.util

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
actual inline fun <reified T : ViewModel> kmpViewModel(): T {
    return hiltViewModel()
}
