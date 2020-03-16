package com.ajcm.kidstube.extensions

import com.ajcm.domain.Video

fun List<Video>.getPositionOf(videoId: String) : Int = this.map { it.videoId }.indexOf(videoId)

fun List<Video>.getNextVideo(videoId: String) : Video {
    val position = getPositionOf(videoId)
    return if ((position - 1) >= this.size || position == -1) {
        this[0]
    } else {
        this[position + 1]
    }
}