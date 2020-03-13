package com.ajcm.kidstube.common

import com.ajcm.domain.Video

sealed class VideoAction(video: Video) {
    data class Play(val video: Video) : VideoAction(video)
    data class Block(val video: Video) : VideoAction(video)
    data class RelatedTo(val video: Video) : VideoAction(video)
}