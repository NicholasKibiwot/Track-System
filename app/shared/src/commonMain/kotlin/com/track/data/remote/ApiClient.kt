package com.track.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
) {
    companion object {
        private const val DEFAULT_BASE_URL = "http://192.168.1.3:8080"
    }

    private val client =
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 3000
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
