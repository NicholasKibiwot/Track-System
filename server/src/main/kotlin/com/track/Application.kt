package com.track

import com.track.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureFirebase()        // 1. Init Firebase Admin SDK first
    configureSerialization()   // 2. JSON content negotiation
    configureCORS()            // 3. Allow Android / web clients
    configureStatusPages()     // 4. Global error handling
    configureWebSockets()      // 5. Real-time GPS streaming
    configureRouting()         // 6. All REST routes last
}
