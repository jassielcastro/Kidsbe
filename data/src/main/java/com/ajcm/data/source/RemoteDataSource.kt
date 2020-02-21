package com.ajcm.data.source

import com.ajcm.domain.Video

interface RemoteDataSource {
    suspend fun searchVideos(apiKey: String, title: String, token: String): List<Video>
    suspend fun getPopularVideos(apiKey: String, relatedToVideoId: String, token: String): List<Video>
}