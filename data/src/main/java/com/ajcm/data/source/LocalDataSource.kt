package com.ajcm.data.source

interface LocalDataSource<T> {

    suspend fun exist(): Boolean
    suspend fun save(obj: T)
    suspend fun getObject(): T
    suspend fun update(obj: T)
    suspend fun delete(obj: T)

}