package com.example.musicplayer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform