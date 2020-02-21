package com.ajcm.usecases

import com.ajcm.data.repository.VideoRepository
import com.ajcm.domain.Video

class GetPopularVideos(private val videoRepository: VideoRepository) {
    suspend fun invoke(relatedTo: String, token: String): List<Video> = videoRepository.getList(relatedTo, "Bearer $token")
}