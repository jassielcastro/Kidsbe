package com.ajcm.data.repository

import com.ajcm.domain.Video

abstract class BaseRemoteRepository {

    abstract suspend fun search(byText: String): List<Video>

    abstract suspend fun getList(relatedTo: String): List<Video>

}