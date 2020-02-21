package com.ajcm.data.repository

import com.ajcm.data.models.Result

abstract class BaseRemoteRepository {

    abstract suspend fun search(byText: String): Result

    abstract suspend fun getList(relatedTo: String): Result

}