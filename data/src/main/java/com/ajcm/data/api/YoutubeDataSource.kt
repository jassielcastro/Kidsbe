package com.ajcm.data.api

import com.ajcm.data.mappers.mapToVideo
import com.ajcm.data.source.RemoteDataSource
import com.ajcm.domain.Video

class YoutubeDataSource(private val api: YoutubeApi): RemoteDataSource {

    override suspend fun searchVideos(apiKey: String, title: String, token: String): List<Video> {
        return api.service
            .searchVideosAsync(apiKey, title, token).await()
            .items.mapToVideo()
    }

    override suspend fun getPopularVideos(apiKey: String, relatedToVideoId: String, token: String): List<Video> {
        return api.service
            .listVideosAsync(apiKey, relatedToVideoId, token).await()
            .items.mapToVideo()
    }
}