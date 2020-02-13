package com.ajcm.usecases

import com.ajcm.data.repository.VideoRepository
import com.ajcm.domain.Video

class GetPopulatVideos(private val videoRepository: VideoRepository) {
    suspend fun invoke(relatedTo: String): List<Video> = videoRepository.getList(relatedTo)
}