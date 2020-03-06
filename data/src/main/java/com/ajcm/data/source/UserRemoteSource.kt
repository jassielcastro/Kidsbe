package com.ajcm.data.source

import com.ajcm.data.api.FirebaseApi
import com.ajcm.data.mappers.toUser
import com.ajcm.domain.User
import kotlinx.coroutines.*
import kotlin.coroutines.resume

class UserRemoteSource(private val api: FirebaseApi): FireBaseDataSource<User?> {

    override suspend fun searchIn(document: String, byReference: String): User? = suspendCancellableCoroutine { continuation ->
        api.db
            .collection(api.url)
            .whereEqualTo(document, byReference)
            .limit(1)
            .get()
            .addOnSuccessListener {
                continuation.resume(it.documents.firstOrNull()?.toUser())
            }.addOnFailureListener {
                continuation.resume(null)
            }
    }

    @InternalCoroutinesApi
    override suspend fun save(value: User?): String = suspendAtomicCancellableCoroutine { continuation ->
        if (value == null) continuation.resume("")
        api.db
            .collection(api.url)
            .add(mapOf(
                "userName" to value!!.userName,
                "userAvatar" to value.userAvatar.name,
                "lastVideoWatched" to value.lastVideoWatched,
                "appEffect" to value.appEffect,
                "soundEffect" to value.soundEffect
            ))
            .addOnCompleteListener {
                continuation.resume(it.result?.id ?: "")
            }
    }

    @InternalCoroutinesApi
    override suspend fun update(value: User?): Boolean = suspendAtomicCancellableCoroutine { continuation ->
        if (value == null) continuation.resume(false)
        api.db
            .collection(api.url)
            .document(value!!.userId)
            .update(mapOf(
                "userName" to value.userName,
                "userAvatar" to value.userAvatar.name,
                "lastVideoWatched" to value.lastVideoWatched,
                "appEffect" to value.appEffect,
                "soundEffect" to value.soundEffect
            ))
            .addOnCompleteListener {
                continuation.resume(it.isSuccessful && it.exception == null)
            }
    }

    override suspend fun getList(): List<User?> {
        return emptyList()
    }
}