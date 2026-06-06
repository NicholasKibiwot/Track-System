package com.track.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * ApiClient configured for a physical phone connecting to a local PC server.
 * If the server is unreachable, calls will fail quickly due to short timeouts,
 * and the UI layer is configured to handle these failures gracefully.
 */
class ApiClient(
    // Your PC IP address on the local network
    private val baseUrl: String = "http://192.168.1.3:8080",
) {
    private val client =
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 3000 // Fast fail (3s) if server is unreachable
                connectTimeoutMillis = 3000
                socketTimeoutMillis = 3000
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }

    suspend fun syncUser(idToken: String): Map<String, Any?> =
        client
            .post("$baseUrl/api/users/sync") {
                header(HttpHeaders.Authorization, "Bearer $idToken")
            }.body()

    suspend fun updateUserProfile(uid: String, phone: String, address: String) {
        client.patch("$baseUrl/api/users/$uid/profile") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "phoneNumber" to phone,
                    "address" to address
                )
            )
        }
    }
}
