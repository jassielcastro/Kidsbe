package com.ajcm.usecases

import com.ajcm.data.repository.VideoRepository
import com.ajcm.domain.Video

class SearchVideos(private val videoRepository: VideoRepository) {
    suspend fun invoke(searchText: String, token: String): List<Video> = videoRepository.search(searchText, token)
}