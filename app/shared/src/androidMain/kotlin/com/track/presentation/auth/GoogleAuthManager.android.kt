package com.track.presentation.auth

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
actual fun rememberGoogleAuthManager(
    onTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
): GoogleAuthManager {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activity = context as Activity
    val webClientId = "582302215652-c2fovnjevegiplri3gdcst4d0gcm8eqa.apps.googleusercontent.com"

    return remember {
        AndroidGoogleAuthManager(activity, webClientId, onTokenReceived, onError, scope)
    }
}

class AndroidGoogleAuthManager(
    private val activity: Activity,
    private val webClientId: String,
    private val onTokenReceived: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val scope: kotlinx.coroutines.CoroutineScope
) : GoogleAuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(activity)

    override fun signIn() {
        scope.launch {
            try {
                val token = signInWithGoogleAndGetFirebaseIdToken()
                onTokenReceived(token)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    override fun signOut() {
        scope.launch {
            auth.signOut()
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) { /* ignore */ }
        }
    }

    private suspend fun signInWithGoogleAndGetFirebaseIdToken(): String {
        val googleOption = GetSignInWithGoogleOption.Builder(
            serverClientId = webClientId,
        ).build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()

        val response = try {
            credentialManager.getCredential(
                context = activity,
                request = request
            )
        } catch (e: GetCredentialException) {
            Log.e("GoogleAuthManager", "getCredential failed", e)
            throw IllegalStateException(
                "Google sign in failed: ${e.message ?: e.javaClass.simpleName}",
                e
            )
        }

        return handleCredentialResponse(response)
    }

    private suspend fun handleCredentialResponse(response: GetCredentialResponse): String {
        val credential = response.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        val googleIdToken = googleIdTokenCredential.idToken
                        Log.d("GoogleAuthManager", "Got Google ID token")

                        return firebaseAuthWithGoogle(googleIdToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("GoogleAuthManager", "Failed to parse GoogleIdTokenCredential", e)
                        throw IllegalStateException("Failed to parse Google credential", e)
                    }
                } else {
                    Log.w("GoogleAuthManager", "Unsupported CustomCredential type: ${credential.type}")
                    throw IllegalStateException("Unsupported credential type: ${credential.type}")
                }
            }

            is PasswordCredential -> {
                val result = auth.signInWithEmailAndPassword(credential.id, credential.password).await()
                val user = result.user ?: throw IllegalStateException("No Firebase user after password sign-in")
                return user.getIdToken(true).await().token ?: throw IllegalStateException("No Firebase ID token")
            }

            else -> {
                Log.w("GoogleAuthManager", "Received unsupported credential class: ${credential.javaClass.simpleName}")
                throw IllegalStateException("Unsupported credential class: ${credential.javaClass.simpleName}")
            }
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String): String {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("No Firebase user after sign-in")
        
        return user.getIdToken(true).await().token
            ?: throw IllegalStateException("Failed to generate sync token")
    }
}
