package com.track.routes

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.track.models.Product
import com.track.models.ProductCategory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.io.File

fun Route.seedRoutes() {
    route("/seed") {
        get("/products") {
            try {
                val db: Firestore = FirestoreClient.getFirestore()
                val jsonString = File("../seed/products.json").readText()
                val products = Json { ignoreUnknownKeys = true }.decodeFromString<List<Product>>(jsonString)

                val batch = db.batch()
                products.forEach { product ->
                    val docRef = db.collection("products").document(product.id)
                    batch.set(docRef, product)
                }
                batch.commit().get()

                call.respond(HttpStatusCode.OK, "Successfully seeded ${products.size} products")
            } catch (e: Exception) {
                application.log.error("Failed to seed products", e)
                call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
            }
        }

        get("/categories") {
            try {
                val db: Firestore = FirestoreClient.getFirestore()
                val jsonString = File("../seed/categories.json").readText()
                val categories = Json { ignoreUnknownKeys = true }.decodeFromString<List<com.track.models.Category>>(jsonString)

                val batch = db.batch()
                categories.forEach { category ->
                    val docRef = db.collection("categories").document(category.id)
                    batch.set(docRef, category)
                }
                batch.commit().get()

                call.respond(HttpStatusCode.OK, "Successfully seeded ${categories.size} categories")
            } catch (e: Exception) {
                application.log.error("Failed to seed categories", e)
                call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
            }
        }
    }
}
