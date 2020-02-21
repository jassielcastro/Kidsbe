package com.ajcm.data.repository

import com.ajcm.domain.Video

abstract class BaseRemoteRepository {

    abstract suspend fun search(byText: String, token: String): List<Video>

    abstract suspend fun getList(relatedTo: String, token: String): List<Video>

}