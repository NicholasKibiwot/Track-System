package com.track.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.InputStream

private val logger = LoggerFactory.getLogger("FirebasePlugin")

/**
 * Initialises the Firebase Admin SDK using the service account key.
 *
 * Priority order:
 *  1. Environment variable FIREBASE_SERVICE_ACCOUNT_JSON (for Cloud Run / CI)
 *  2. Local file serviceAccountKey.json in the working directory (local dev)
 *
 * IMPORTANT: never commit serviceAccountKey.json to Git.
 */
fun Application.configureFirebase() {
    if (FirebaseApp.getApps().isNotEmpty()) {
        logger.info("Firebase already initialized — skipping")
        return
    }

    val serviceAccountStream: InputStream = run {
        val envJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON")
        if (!envJson.isNullOrBlank()) {
            logger.info("Loading Firebase credentials from environment variable")
            envJson.byteInputStream()
        } else {
            logger.info("Loading Firebase credentials from serviceAccountKey.json")
            FileInputStream("serviceAccountKey.json")
        }
    }

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
        .setProjectId("yhecutmedia-92d9a")
        .build()

    FirebaseApp.initializeApp(options)
    logger.info("Firebase Admin SDK initialised for project: yhecutmedia-92d9a")
}
