package com.ajcm.data.repository

import com.ajcm.data.source.RemoteDataSource
import com.ajcm.domain.Video

class VideoRepository(
    private val remoteDataSource: RemoteDataSource,
    private val apiKey: String
): BaseRemoteRepository() {

    override suspend fun search(byText: String): List<Video> {
        return remoteDataSource.searchVideos(apiKey, byText)
    }

    override suspend fun getList(relatedTo: String): List<Video> {
        return remoteDataSource.getPopularVideos(apiKey, relatedTo)
    }
}