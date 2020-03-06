package com.ajcm.data.source

import com.ajcm.domain.User

interface LocalDataSource {

    suspend fun existUser(): Boolean
    suspend fun saveUser(user: User)
    suspend fun getUser(): User
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)

}