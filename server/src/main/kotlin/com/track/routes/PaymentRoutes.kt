package com.track.routes

import com.google.firebase.cloud.FirestoreClient
import com.track.models.Payment
import com.track.models.PaymentStatus
import com.track.models.Order
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.paymentRoutes() {
    val db = FirestoreClient.getFirestore()

    route("/api/payments") {
        // M-Pesa Callback or Payment Verification
        post("/verify") {
            try {
                val body = call.receive<Map<String, Any>>()
                val orderId = body["orderId"] as? String ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing orderId")
                val transactionId = body["transactionId"] as? String ?: "TXN-" + System.currentTimeMillis()
                val success = body["success"] as? Boolean ?: false

                val paymentStatus = if (success) PaymentStatus.PAID else PaymentStatus.FAILED
                
                // 1. Log payment
                val payment = Payment(
                    id = UUID_JVM(),
                    orderId = orderId,
                    amount = (body["amount"] as? Number)?.toDouble() ?: 0.0,
                    status = paymentStatus,
                    transactionId = transactionId,
                    createdAt = System.currentTimeMillis()
                )
                db.collection("payments").document(payment.id).set(payment).get()

                // 2. Update Order if successful
                if (success) {
                    db.collection("orders").document(orderId).update("paymentStatus", PaymentStatus.PAID).get()
                }

                call.respond(HttpStatusCode.OK, mapOf("status" to paymentStatus.name, "transactionId" to transactionId))
            } catch (e: Exception) {
                application.log.error("Payment verification failed", e)
                call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
            }
        }
    }
}

private fun UUID_JVM() = java.util.UUID.randomUUID().toString()
