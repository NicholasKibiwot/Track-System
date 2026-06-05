package com.track.presentation.auth

import android.app.Activity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

/**
 * Manages Google Sign-In using the modern Credential Manager API.
 * All legacy GoogleSignInClient code has been removed.
 */
class GoogleAuthManager(
    private val activity: Activity,
    private val webClientId: String,
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(activity)

    /**
     * recommended: Sign in using the modern Credential Manager API.
     */
    suspend fun signInWithCredentialManager(): String {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            context = activity,
            request = request
        )

        val credential = result.credential
        if (credential is GoogleIdTokenCredential) {
            return firebaseAuthWithGoogle(credential.idToken)
        } else {
            throw Exception("Received unexpected credential type: ${credential.type}")
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
