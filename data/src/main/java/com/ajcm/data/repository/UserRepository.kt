package com.ajcm.data.repository

import com.ajcm.data.source.UserRemoteSource
import com.ajcm.domain.User

class UserRepository(private val remoteDataSource: UserRemoteSource) {

    suspend fun searchIn(document: String, byReference: String): User? {
        return remoteDataSource.searchIn(document, byReference)
    }

    suspend fun save(user: User): String {
        return remoteDataSource.save(user)
    }

    suspend fun update(user: User): Boolean {
        return remoteDataSource.update(user)
    }

}