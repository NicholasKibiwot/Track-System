package com.track.routes

import com.google.cloud.Timestamp
import com.google.cloud.firestore.Query
import com.google.firebase.cloud.FirestoreClient
import com.track.models.CheckIn
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.checkInRoutes() {
    val db = FirestoreClient.getFirestore()

    route("/api/checkins") {

        // Create a user check-in (manual tap or automatic geo-trigger)
        post {
            val checkIn = call.receive<CheckIn>()
            require(checkIn.userUid.isNotBlank()) { "userUid must not be blank" }
            require(checkIn.type in listOf("manual", "auto")) {
                "type must be: manual | auto"
            }

            val data = mapOf(
                "userUid" to checkIn.userUid,
                "latitude" to checkIn.latitude,
                "longitude" to checkIn.longitude,
                "locationName" to checkIn.locationName,
                "timestamp" to Timestamp.now(),
                "type" to checkIn.type
            )
            val ref = db.collection("checkIns").add(data).get()
            call.respond(HttpStatusCode.Created, mapOf("checkInId" to ref.id, "status" to "checked in"))
        }

        // Get all check-ins for a specific user
        get("/user/{uid}") {
            val uid = call.parameters["uid"]!!
            val snapshot = db.collection("checkIns")
                .whereEqualTo("userUid", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get().get()
            call.respond(HttpStatusCode.OK, snapshot.documents.map { it.data })
        }
    }
}
