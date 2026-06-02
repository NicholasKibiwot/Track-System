package com.track

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform