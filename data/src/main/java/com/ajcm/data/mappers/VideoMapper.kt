package com.ajcm.data.mappers

import com.ajcm.data.models.VideoItem
import com.ajcm.domain.Video
import com.google.api.services.youtube.model.SearchResult
import com.google.firebase.firestore.DocumentSnapshot

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

fun List<SearchResult>.mapToList(): List<Video> {
    return this.map { item ->
        Video(
            item.id.videoId,
            item.snippet.channelId ?: "",
            item.snippet.title ?: "",
            item.snippet.thumbnails?.high?.url ?: "",
            item.snippet.channelTitle ?: ""
        )
    }
}

fun List<DocumentSnapshot>.transformList(): List<Video> {
    return this.map { item ->
        Video(
            item["videoId"].toString(),
            item["channelId"].toString(),
            item["title"].toString(),
            item["thumbnail"].toString(),
            item["channelTitle"].toString()
        )
    }
}