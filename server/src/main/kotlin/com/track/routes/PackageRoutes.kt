package com.track.routes

import com.google.cloud.Timestamp
import com.google.firebase.cloud.FirestoreClient
import com.track.models.DeliveryPackage
import com.track.models.VehicleLocation
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.packageRoutes() {
    val db = FirestoreClient.getFirestore()

    route("/api/packages") {

        // List all packages
        get {
            val snapshot = db.collection("packages").get().get()
            call.respond(HttpStatusCode.OK, snapshot.documents.map { it.data })
        }

        // Create a new package (called when sender books delivery)
        post {
            val pkg = call.receive<DeliveryPackage>()
            require(pkg.id.isNotBlank()) { "Package id must not be blank" }
            require(pkg.senderUid.isNotBlank()) { "senderUid must not be blank" }
            val data = mapOf(
                "id" to pkg.id,
                "description" to pkg.description,
                "senderUid" to pkg.senderUid,
                "receiverUid" to pkg.receiverUid,
                "assignedVehicleId" to pkg.assignedVehicleId,
                "status" to "pending",
                "currentLatitude" to 0.0,
                "currentLongitude" to 0.0,
                "updatedAt" to Timestamp.now()
            )
            db.collection("packages").document(pkg.id).set(data).get()
            call.respond(HttpStatusCode.Created, data)
        }

        route("/{packageId}") {

            // Get a single package (customer tracks their parcel)
            get {
                val id = call.parameters["packageId"]!!
                val doc = db.collection("packages").document(id).get().get()
                if (!doc.exists()) throw NoSuchElementException("Package $id not found")
                call.respond(HttpStatusCode.OK, doc.data ?: emptyMap<String, Any>())
            }

            // Update status (driver/staff app changes delivery state)
            patch("/status") {
                val id = call.parameters["packageId"]!!
                val body = call.receive<Map<String, String>>()
                val status = body["status"] ?: throw IllegalArgumentException("Missing status")
                require(status in listOf("pending", "in_transit", "delivered")) {
                    "status must be: pending | in_transit | delivered"
                }
                db.collection("packages").document(id).update(
                    mapOf("status" to status, "updatedAt" to Timestamp.now())
                ).get()
                call.respond(HttpStatusCode.OK, mapOf("packageId" to id, "status" to status))
            }

            // Update GPS location of a package in transit
            post("/location") {
                val id = call.parameters["packageId"]!!
                val loc = call.receive<VehicleLocation>()
                db.collection("packages").document(id).update(
                    mapOf(
                        "currentLatitude" to loc.latitude,
                        "currentLongitude" to loc.longitude,
                        "updatedAt" to Timestamp.now()
                    )
                ).get()
                call.respond(HttpStatusCode.OK, mapOf("status" to "package location updated"))
            }
        }
    }
}
