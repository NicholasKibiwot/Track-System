package com.track.presentation.customer

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.track.data.remote.ApiClient
import com.track.presentation.viewmodel.AppAuthViewModel
import kotlinx.coroutines.launch

@Composable
fun CustomerLoginContainer(
    webClientId: String,
    onProfileCompleted: () -> Unit,
    onNavigateToCompleteProfile: (uid: String) -> Unit,
    onForgotPasswordClick: () -> Unit = {},
    onBackClick: () -> Unit,
    authViewModel: AppAuthViewModel
) {
    val scope = rememberCoroutineScope()
    val api = remember { ApiClient() }
    var isSyncing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        CustomerLoginScreen(
            webClientId = webClientId,
            onBackClick = onBackClick,
            onForgotPasswordClick = onForgotPasswordClick,
            onLoggedIn = { idToken ->
                scope.launch {
                    try {
                        isSyncing = true
                        error = null
                        Log.d("LoginContainer", "Syncing user with backend...")
                        val profile = try { api.syncUser(idToken) } catch (e: Exception) { null }
                        Log.d("LoginContainer", "User synced: $profile")
                        
                        // If backend sync fails, ensure we still have a profile in Firestore
                        authViewModel.refreshProfile(idToken)

                        onProfileCompleted()
                    } catch (e: Exception) {
                        Log.e("LoginContainer", "Login failed", e)
                        error = "Login Failed: ${e.localizedMessage}"
                    } finally {
                        isSyncing = false
                    }
                }
            },
            onEmailSignIn = { email, password ->
                authViewModel.login(email, password) {
                    onProfileCompleted()
                }
            },
            onEmailSignUp = { email, password ->
                authViewModel.register(email, password, "", "") {
                    onProfileCompleted()
                }
            }
        )

        if (isSyncing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Syncing with server...", color = Color.White)
                }
            }
        }
        
        error?.let {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Text(text = it)
            }
        }
    }
}
