package com.ajcm.data.api

import com.ajcm.data.mappers.transformList
import com.ajcm.data.models.Result
import com.ajcm.data.source.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class FirebaseDataSource(private val api: FirebaseApi): RemoteDataSource {

    override suspend fun searchVideos(apiKey: String, title: String): Result {
        val request = withContext(Dispatchers.IO) {
            async {
                api.db
                    .collection(api.url)
                    .get()
            }
        }
        val r = request.await().result?.documents
        return Result(r?.transformList() ?: arrayListOf(), null)
    }

    override suspend fun getPopularVideos(apiKey: String, relatedToVideoId: String): Result {
        val request = withContext(Dispatchers.IO) {
            async {
                api.db
                    .collection(api.url)
                    .get()
            }
        }
        val r = request.await().result?.documents
        return Result(r?.transformList() ?: arrayListOf(), null)
    }
}