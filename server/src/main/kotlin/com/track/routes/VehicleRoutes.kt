package com.track.routes

import com.google.cloud.Timestamp
import com.google.cloud.firestore.Query
import com.google.firebase.cloud.FirestoreClient
import com.track.models.Vehicle
import com.track.models.VehicleLocation
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay

fun Route.vehicleRoutes() {
    val db = FirestoreClient.getFirestore()

    route("/api/vehicles") {
        get {
            val snapshot = db.collection("vehicles").get().get()
            call.respond(HttpStatusCode.OK, snapshot.documents.map { it.data })
        }

        post {
            val vehicle = call.receive<Vehicle>()
            require(vehicle.id.isNotBlank()) { "Vehicle id must not be blank" }
            val data = mapOf(
                "id" to vehicle.id,
                "name" to vehicle.name,
                "plateNumber" to vehicle.plateNumber,
                "driverUid" to vehicle.driverUid,
                "status" to "idle",
                "currentLatitude" to 0.0,
                "currentLongitude" to 0.0,
                "lastUpdated" to Timestamp.now()
            )
            db.collection("vehicles").document(vehicle.id).set(data).get()
            call.respond(HttpStatusCode.Created, data)
        }

        route("/{vehicleId}") {
            vehicleInstanceRoutes(db)
        }
    }
}

private fun Route.vehicleInstanceRoutes(db: com.google.cloud.firestore.Firestore) {
    get {
        val id = call.parameters["vehicleId"]!!
        val doc = db.collection("vehicles").document(id).get().get()
        if (!doc.exists()) throw NoSuchElementException("Vehicle $id not found")
        call.respond(HttpStatusCode.OK, doc.data ?: emptyMap<String, Any>())
    }

    patch("/status") {
        val id = call.parameters["vehicleId"]!!
        val body = call.receive<Map<String, String>>()
        val status = body["status"] ?: throw IllegalArgumentException("Missing status")
        require(status in listOf("active", "idle", "offline")) {
            "status must be: active | idle | offline"
        }
        db.collection("vehicles").document(id)
            .update(mapOf("status" to status, "lastUpdated" to Timestamp.now())).get()
        call.respond(HttpStatusCode.OK, mapOf("vehicleId" to id, "status" to status))
    }

    vehicleLocationRoutes(db)
}

private fun Route.vehicleLocationRoutes(db: com.google.cloud.firestore.Firestore) {
    post("/location") {
        val id = call.parameters["vehicleId"]!!
        val loc = call.receive<VehicleLocation>()

        val locationData = mapOf(
            "latitude" to loc.latitude,
            "longitude" to loc.longitude,
            "speed" to loc.speed,
            "heading" to loc.heading,
            "timestamp" to Timestamp.now()
        )

        db.collection("vehicles").document(id).collection("locations").add(locationData).get()
        db.collection("vehicles").document(id).update(
            mapOf(
                "currentLatitude" to loc.latitude,
                "currentLongitude" to loc.longitude,
                "status" to "active",
                "lastUpdated" to Timestamp.now()
            )
        ).get()

        call.respond(HttpStatusCode.OK, mapOf("status" to "location updated"))
    }

    get("/location") {
        val id = call.parameters["vehicleId"]!!
        val doc = db.collection("vehicles").document(id).get().get()
        if (!doc.exists()) throw NoSuchElementException("Vehicle $id not found")
        val data = doc.data ?: emptyMap()
        call.respond(HttpStatusCode.OK, mapOf(
            "vehicleId" to id,
            "latitude" to (data["currentLatitude"] ?: 0.0),
            "longitude" to (data["currentLongitude"] ?: 0.0),
            "lastUpdated" to (data["lastUpdated"]?.toString() ?: "")
        ))
    }

    get("/history") {
        val id = call.parameters["vehicleId"]!!
        val history = db.collection("vehicles").document(id)
            .collection("locations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)
            .get().get()
        call.respond(HttpStatusCode.OK, history.documents.map { it.data })
    }

    webSocket("/stream") {
        val id = call.parameters["vehicleId"]!!
        send("Connected to vehicle $id live stream")
        try {
            while (true) {
                val doc = db.collection("vehicles").document(id).get().get()
                if (doc.exists()) {
                    val data = doc.data
                    val payload = """{"vehicleId":"$id","lat":${data?.get("currentLatitude")},"lng":${data?.get("currentLongitude")},"status":"${data?.get("status")}"}"""
                    send(payload)
                }
                delay(3000)
            }
        } catch (e: Exception) {
            close(CloseReason(CloseReason.Codes.NORMAL, "Stream ended"))
        }
    }
}
