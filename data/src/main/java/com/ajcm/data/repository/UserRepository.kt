package com.ajcm.data.repository

import com.ajcm.data.source.UserRemoteSource
import com.ajcm.domain.User
import kotlinx.coroutines.InternalCoroutinesApi

class UserRepository(private val remoteDataSource: UserRemoteSource) {

    suspend fun searchIn(document: String, byReference: String): User? {
        return remoteDataSource.searchIn(document, byReference)
    }

    @InternalCoroutinesApi
    suspend fun save(user: User): String {
        return remoteDataSource.save(user)
    }

    @InternalCoroutinesApi
    suspend fun update(user: User): Boolean {
        return remoteDataSource.update(user)
    }

}