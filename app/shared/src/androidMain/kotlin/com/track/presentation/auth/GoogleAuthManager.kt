package com.track.presentation.auth

import android.app.Activity
import android.util.Log
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
import kotlinx.coroutines.tasks.await

/**
 * Manages Google Sign-In using the modern Credential Manager API.
 */
class GoogleAuthManager(
    private val activity: Activity,
    private val webClientId: String,
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(activity)

    /**
     * Recommended: Sign in using the modern Credential Manager API.
     * Returns a fresh Firebase ID token for the signed‑in user.
     */
    suspend fun signInWithGoogleAndGetFirebaseIdToken(): String {
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
        
        // Return Firebase ID token for Ktor sync
        return user.getIdToken(true).await().token
            ?: throw IllegalStateException("Failed to generate sync token")
    }

    fun currentUser() = auth.currentUser

    suspend fun signOut() {
        auth.signOut()
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) { /* ignore */ }
    }
}
