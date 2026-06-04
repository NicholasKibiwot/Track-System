package com.track.routes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.cloud.FirestoreClient
import com.track.models.User
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * User routes — called when a customer logs in via Google on the Android app.
 *
 * Flow:
 *  1. Android signs in with Google → gets a Firebase ID token
 *  2. Android sends POST /api/users/sync with the ID token in the Authorization header
 *  3. Server verifies the token, creates/updates the Firestore user document
 *  4. Returns the user profile
 */
fun Route.userRoutes() {
    val db = FirestoreClient.getFirestore()
    val auth = FirebaseAuth.getInstance()

    route("/api/users") {

        // Called by Android after Google Sign-In to sync the user profile
        post("/sync") {
            val idToken = call.request.header("Authorization")
                ?.removePrefix("Bearer ")
                ?: throw IllegalArgumentException("Missing Authorization header")

            // Verify the Google ID token server-side
            val decoded = auth.verifyIdToken(idToken)

            val userData = mapOf(
                "uid" to decoded.uid,
                "displayName" to (decoded.name ?: ""),
                "email" to (decoded.email ?: ""),
                "photoUrl" to (decoded.picture ?: ""),
                "role" to "customer"
            )

            // Merge so existing data (phone, role) is not overwritten
            db.collection("users").document(decoded.uid)
                .set(userData, com.google.cloud.firestore.SetOptions.merge())
                .get()

            call.respond(HttpStatusCode.OK, userData)
        }

        // Get a user profile by UID
        get("/{uid}") {
            val uid = call.parameters["uid"]
                ?: throw IllegalArgumentException("Missing uid")
            val doc = db.collection("users").document(uid).get().get()
            if (!doc.exists()) throw NoSuchElementException("User $uid not found")
            call.respond(HttpStatusCode.OK, doc.data ?: emptyMap<String, Any>())
        }

        // Update user role (admin only in production — add auth guard later)
        patch("/{uid}/role") {
            val uid = call.parameters["uid"]
                ?: throw IllegalArgumentException("Missing uid")
            val body = call.receive<Map<String, String>>()
            val role = body["role"] ?: throw IllegalArgumentException("Missing role field")
            require(role in listOf("customer", "driver", "admin")) {
                "role must be one of: customer, driver, admin"
            }
            db.collection("users").document(uid).update("role", role).get()
            call.respond(HttpStatusCode.OK, mapOf("uid" to uid, "role" to role))
        }
    }
}
