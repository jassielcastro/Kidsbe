package com.ajcm.data.repository

import com.ajcm.data.source.VideoWatchedDataSource
import com.ajcm.domain.Video

class VideoWatchedRepository(private val remoteDataSource: VideoWatchedDataSource) {

    suspend fun save(video: Video): String {
        return remoteDataSource.save(video)
    }

    suspend fun update(video: Video): Boolean {
        return remoteDataSource.update(video)
    }

    suspend fun addToBlackList(byText: String) {
        remoteDataSource.addToBlackList(byText)
    }

}