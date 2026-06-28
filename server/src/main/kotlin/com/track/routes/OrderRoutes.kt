package com.track.routes

import com.google.firebase.cloud.FirestoreClient
import com.track.models.Order
import com.track.models.OrderStatus
import com.track.models.PaymentStatus
import com.track.util.TrackTimestamp
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.orderRoutes() {
    val db = FirestoreClient.getFirestore()

    route("/api/orders") {
        // Create new order with tracking number generation
        post {
            try {
                val order = call.receive<Order>()
                val trackingNumber = "TRK-" + UUID.randomUUID().toString().take(8).uppercase()
                val now = System.currentTimeMillis()
                val timestamp = TrackTimestamp(now / 1000, ((now % 1000) * 1_000_000).toInt())
                
                val finalOrder = order.copy(
                    id = order.id.ifBlank { UUID.randomUUID().toString() },
                    trackingNumber = trackingNumber,
                    orderStatus = OrderStatus.PENDING,
                    createdAt = timestamp,
                    updatedAt = timestamp
                )
                
                db.collection("orders").document(finalOrder.id).set(finalOrder).get()
                call.respond(HttpStatusCode.Created, finalOrder)
            } catch (e: Exception) {
                application.log.error("Failed to create order", e)
                call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
            }
        }

        // Get order by ID
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ID")
            val doc = db.collection("orders").document(id).get().get()
            if (doc.exists()) {
                call.respond(HttpStatusCode.OK, doc.toObject(Order::class.java)!!)
            } else {
                call.respond(HttpStatusCode.NotFound, "Order not found")
            }
        }

        // Update status
        patch("/{id}/status") {
            val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing ID")
            val body = call.receive<Map<String, String>>()
            val statusStr = body["status"] ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing status")
            
            try {
                val status = OrderStatus.valueOf(statusStr.uppercase())
                db.collection("orders").document(id).update("orderStatus", status).get()
                call.respond(HttpStatusCode.OK, "Status updated to $status")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid status: $statusStr")
            }
        }
    }
}
