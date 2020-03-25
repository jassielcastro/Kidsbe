package com.ajcm.data.source

import com.ajcm.data.models.Result

interface RemoteDataSource {
    suspend fun searchVideos(apiKey: String, title: String): Result
    suspend fun getPopularVideos(apiKey: String, relatedToVideoId: String, attempts: Int): Result
}