plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
}

group = "com.track"
version = "1.0.0"

application {
    mainClass = "com.track.ApplicationKt"
}

dependencies {
    api(projects.core)

    // Logging
    implementation(libs.logback)

    // Ktor server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.call.logging)

    // Firebase Admin SDK  (server-side only — uses Java/JVM)
    implementation("com.google.firebase:firebase-admin:9.3.0")

    // Kotlin serialization & coroutines
    implementation(libs.kotlinx.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.testJunit)
}
