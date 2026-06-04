package com.track.plugins

import com.track.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Health check
        get("/") {
            call.respondText("Track-System API is running ✓")
        }
        get("/health") {
            call.respondText("OK")
        }

        // Feature routes
        userRoutes()
        vehicleRoutes()
        packageRoutes()
        checkInRoutes()
    }
}
