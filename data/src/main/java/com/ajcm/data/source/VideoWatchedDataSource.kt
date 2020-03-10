package com.ajcm.data.source

import com.ajcm.data.api.FirebaseApi
import com.ajcm.domain.Video
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class VideoWatchedDataSource(private val api: FirebaseApi): FireBaseDataSource<Video?> {

    // not used yet
    override suspend fun searchIn(document: String, byReference: String): Video? {
        return null
    }

    override suspend fun save(value: Video?): String  = suspendCancellableCoroutine { continuation ->
        if (value == null) continuation.resume("failure")
        api.db
            .collection(api.url)
            .document(value!!.videoId)
            .set(mapOf(
                "title" to value.title,
                "thumbnail" to value.thumbnail,
                "channelId" to value.channelId,
                "channelTitle" to value.channelTitle
            ))
            .addOnCompleteListener {
                continuation.resume("success")
            }
    }

    override suspend fun update(value: Video?): Boolean {
        return false
    }

    override suspend fun getList(): List<Video?> {
        return listOf()
    }

}