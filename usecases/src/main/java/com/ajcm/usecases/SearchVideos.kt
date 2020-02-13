package com.ajcm.usecases

import com.ajcm.data.repository.VideoRepository
import com.ajcm.domain.Video

class SearchVideos(private val videoRepository: VideoRepository) {
    suspend fun invoke(searchText: String): List<Video> = videoRepository.search(searchText)
}