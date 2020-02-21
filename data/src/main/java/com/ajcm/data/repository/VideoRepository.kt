package com.ajcm.data.repository

import com.ajcm.data.source.RemoteDataSource
import com.ajcm.domain.Video

class VideoRepository(
    private val remoteDataSource: RemoteDataSource,
    private val apiKey: String
): BaseRemoteRepository() {

    override suspend fun search(byText: String, token: String): List<Video> {
        return remoteDataSource.searchVideos(apiKey, byText, token)
    }

    override suspend fun getList(relatedTo: String, token: String): List<Video> {
        return remoteDataSource.getPopularVideos(apiKey, relatedTo, token)
    }
}