package com.ajcm.kidstube.model

import com.ajcm.domain.Video
import java.io.Serializable

data class VideoList(val list: List<Video>) : Serializable