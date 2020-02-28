package com.ajcm.data.repository

import com.ajcm.data.source.UserRemoteSource
import com.ajcm.domain.User
import kotlinx.coroutines.InternalCoroutinesApi

class UserRepository(private val remoteDataSource: UserRemoteSource) {

    suspend fun search(id: String): User? {
        return remoteDataSource.searchDocument(id)
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