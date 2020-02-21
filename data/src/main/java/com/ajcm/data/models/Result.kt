package com.ajcm.data.models

import com.ajcm.domain.Video

data class Result(
    val videos: List<Video>,
    val exception: Exception?
)