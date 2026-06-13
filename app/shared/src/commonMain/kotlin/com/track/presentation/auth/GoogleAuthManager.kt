package com.track.presentation.auth

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGoogleAuthManager(
    onTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
): GoogleAuthManager

interface GoogleAuthManager {
    fun signIn()
    fun signOut()
}

