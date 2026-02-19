package com.example.newsfeedsim

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform