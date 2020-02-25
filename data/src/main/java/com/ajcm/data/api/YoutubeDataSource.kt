package com.ajcm.data.api

import com.ajcm.data.mappers.mapToVideo
import com.ajcm.data.models.Result
import com.ajcm.data.source.RemoteDataSource

class YoutubeDataSource(private val api: YoutubeApi): RemoteDataSource {

    override suspend fun searchVideos(apiKey: String, title: String): Result {
        return try {
            val list = api.service
                .searchVideosAsync(apiKey, title).await()
                .items.mapToVideo()
            Result(list, null)
        } catch (e: Exception) {
            Result(arrayListOf(), e)
        }
    }

    override suspend fun getPopularVideos(apiKey: String, relatedToVideoId: String): Result {
        return try {
            val list = api.service
                .listVideosAsync(apiKey, relatedToVideoId).await()
                .items.mapToVideo()
            Result(list, null)
        } catch (e: Exception) {
            Result(arrayListOf(), e)
        }
    }
}