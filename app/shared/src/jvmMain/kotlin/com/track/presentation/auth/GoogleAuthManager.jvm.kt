package com.track.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberGoogleAuthManager(
    onTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
): GoogleAuthManager {
    return remember {
        JvmGoogleAuthManager(onTokenReceived, onError)
    }
}

class JvmGoogleAuthManager(
    private val onTokenReceived: (String) -> Unit,
    private val onError: (String) -> Unit
) : GoogleAuthManager {
    override fun signIn() {
        onError("Google Sign-In is not yet implemented for Desktop.")
    }

    override fun signOut() {
    }
}
