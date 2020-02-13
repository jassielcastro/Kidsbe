package com.ajcm.data.source

import com.ajcm.domain.Video

interface RemoteDataSource {
    suspend fun searchVideos(apiKey: String, title: String): List<Video>
    suspend fun getPopularVideos(apiKey: String, relatedToVideoId: String): List<Video>
}