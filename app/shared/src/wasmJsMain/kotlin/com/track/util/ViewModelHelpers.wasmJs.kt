package com.track.util

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
actual inline fun <reified T : ViewModel> kmpViewModel(): T {
    return viewModel()
}
