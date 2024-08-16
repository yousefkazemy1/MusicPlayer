package com.example.musicplayer.data.remote

import com.example.musicplayer.data.remote.Endpoints.AUTHORIZATION
import com.example.musicplayer.data.remote.Endpoints.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val ktorInstance = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            useAlternativeNames = false
        })
    }

    defaultRequest {
        url {
            host = BASE_URL
            protocol = URLProtocol.HTTPS
        }
        headers {
            append("Authorization", AUTHORIZATION)
        }
    }
}