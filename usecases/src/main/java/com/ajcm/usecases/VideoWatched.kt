package com.ajcm.usecases

import com.ajcm.data.repository.VideoWatchedRepository
import com.ajcm.domain.Video

class VideoWatched(private val vwRepository: VideoWatchedRepository) {

    suspend fun save(video: Video): String = vwRepository.save(video)

    suspend fun addToBlackList(byText: String) = vwRepository.addToBlackList(byText)

}