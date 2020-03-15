package com.ajcm.kidstube.common

import com.ajcm.domain.Video

sealed class VideoAction {
    data class Play(val video: Video) : VideoAction()
    data class Block(val video: Video) : VideoAction()
    data class RelatedTo(val video: Video) : VideoAction()
}