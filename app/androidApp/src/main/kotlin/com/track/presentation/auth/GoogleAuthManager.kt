package com.track.presentation.auth

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(
    private val activity: Activity,
    private val webClientId: String,
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val signInClient: GoogleSignInClient by lazy {
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId) // from Firebase console
                .requestEmail()
                .build()
        GoogleSignIn.getClient(activity, gso)
    }

    fun getSignInIntent(): Intent = signInClient.signInIntent

    suspend fun handleSignInResult(data: Intent?): String {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.await()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("No Firebase user after sign-in")
        // Return Firebase ID token to send to Ktor backend
        return user.getIdToken(true).await().token
            ?: throw IllegalStateException("No ID token")
    }

    fun currentUser() = auth.currentUser

    suspend fun signOut() {
        auth.signOut()
        signInClient.signOut().await()
    }
}
