package com.ajcm.data.source

interface FireBaseDataSource<T> {
    suspend fun searchIn(document: String, byReference: String): T
    suspend fun save(value: T): String
    suspend fun update(value: T): Boolean
    suspend fun getList(): List<T>
}