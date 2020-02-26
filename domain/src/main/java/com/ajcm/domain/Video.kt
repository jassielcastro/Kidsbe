package com.ajcm.domain

import java.io.Serializable

data class Video(
    val videoId: String,
    val channelId: String,
    val title: String,
    val thumbnail: String,
    val channelTitle: String
) : Serializable