package com.ajcm.usecases

import com.ajcm.data.repository.VideoWatchedRepository
import com.ajcm.domain.Video

class SaveVideoWatched(private val vwRepository: VideoWatchedRepository) {

    suspend fun invoke(video: Video): String = vwRepository.save(video)

}