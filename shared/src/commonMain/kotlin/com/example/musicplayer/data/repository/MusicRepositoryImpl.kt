package com.example.musicplayer.data.repository

import com.example.musicplayer.data.remote.Endpoints.MUSICS
import com.example.musicplayer.data.remote.response.MusicResponse
import com.example.musicplayer.data.remote.response.mapToDomain
import com.example.musicplayer.domain.model.Music
import com.example.musicplayer.domain.repository.MusicRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

class MusicRepositoryImpl(
    private val httpClient: HttpClient
) : MusicRepository {
    override suspend fun getMusicsList(): List<Music> {
        return try {
            val response = httpClient.get {
                url {
                    path(MUSICS)
                    parameters.append("query", "nature")
                    parameters.append("per_page", "10")
                }
            }.body<List<MusicResponse>>()
            response.mapToDomain()
        } catch (e: Exception) {
            emptyList()
        }
    }
}