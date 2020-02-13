package com.ajcm.data.mappers

import com.ajcm.data.models.VideoItem
import com.ajcm.domain.Video

fun List<VideoItem>.mapToVideo(): List<Video> {
    return this.map { item ->
        Video(
            item.videoId.videoId,
            item.snippet.channelId ?: "",
            item.snippet.title ?: "",
            item.snippet.thumbnails?.default?.url ?: "",
            item.snippet.channelTitle ?: ""
        )
    }
}