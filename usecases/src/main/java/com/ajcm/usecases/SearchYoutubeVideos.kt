package com.ajcm.usecases

import com.ajcm.data.source.YoutubeRemoteSource
import com.ajcm.data.models.Result
import com.ajcm.data.repository.VideoRepository

class SearchYoutubeVideos(private val videoRepository: VideoRepository) {

    fun startYoutubeWith(accountName: String) = (videoRepository.remoteDataSource as? YoutubeRemoteSource)?.startYoutubeSession(accountName)

    suspend fun invoke(searchText: String): Result = videoRepository.search(searchText)
}