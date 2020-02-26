package com.ajcm.usecases

import com.ajcm.data.models.Result
import com.ajcm.data.repository.VideoRepository

class GetFirestoreVideos(private val videoRepository: VideoRepository) {

    suspend fun invoke(relatedTo: String): Result = videoRepository.getList(relatedTo)

}