package com.track.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberGoogleAuthManager(
    onTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
): GoogleAuthManager {
    return remember {
        JsGoogleAuthManager(onTokenReceived, onError)
    }
}

class JsGoogleAuthManager(
    private val onTokenReceived: (String) -> Unit,
    private val onError: (String) -> Unit
) : GoogleAuthManager {
    override fun signIn() {
        onError("Google Sign-In is not yet implemented for Web.")
    }

    override fun signOut() {
        // Sign-out logic for Web is not yet implemented.
        onError("Google Sign-Out is not yet implemented for Web.")
    }
}
