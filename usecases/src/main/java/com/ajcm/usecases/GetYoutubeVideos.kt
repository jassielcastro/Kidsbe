package com.ajcm.usecases

import com.ajcm.data.source.YoutubeRemoteSource
import com.ajcm.data.models.Result
import com.ajcm.data.repository.VideoRepository

class GetYoutubeVideos(private val videoRepository: VideoRepository) {

    suspend fun startYoutubeWith(accountName: String) = (videoRepository.remoteDataSource as? YoutubeRemoteSource)?.startYoutubeSession(accountName)

    suspend fun invoke(relatedTo: String): Result = videoRepository.getList(relatedTo)
}