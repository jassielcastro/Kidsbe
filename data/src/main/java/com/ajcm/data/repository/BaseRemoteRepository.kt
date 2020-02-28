package com.ajcm.data.repository

abstract class BaseRemoteRepository<T> {

    abstract suspend fun search(byText: String): T

}