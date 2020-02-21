package com.ajcm.data.api

import android.app.Application
import com.ajcm.data.auth.Session
import com.ajcm.data.mappers.mapToList
import com.ajcm.data.models.Result
import com.ajcm.data.source.RemoteDataSource
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

class YoutubeRemoteSource(private val application: Application): RemoteDataSource {

    private lateinit var youtube: YouTube.Search.List

    fun startYoutubeSession(accountName: String) {
        youtube = Session(application, accountName)
            .getYoutubeSession()
            .Search()
            .list("id,snippet")
            .setType("video")
            .setMaxResults(25)
            .setSafeSearch("strict")
            .setFields("items(id/kind,id/videoId,snippet/channelId,snippet/title,snippet/thumbnails/high/url,snippet/channelTitle)")
    }

    override suspend fun searchVideos(apiKey: String, title: String): Result {
        return try {
            val list = youtube
                .setKey(apiKey)
                .setQ(title)

            val request = withContext(Dispatchers.IO) {
                async {
                    list.execute()
                }
            }
            val r = request.await()
            Result(r.items.mapToList(), null)
        } catch (e: UserRecoverableAuthIOException) {
            Result(arrayListOf(), e)
        } catch (e: GooglePlayServicesAvailabilityIOException) {
            Result(arrayListOf(), e)
        } catch (e: IOException) {
            Result(arrayListOf(), e)
        }
    }

    override suspend fun getPopularVideos(apiKey: String, relatedToVideoId: String): Result {
        return try {
            val list = youtube
                .setKey(apiKey)
                .setRelatedToVideoId(relatedToVideoId)

            val request = withContext(Dispatchers.IO) {
                async {
                    list.execute()
                }
            }
            val r = request.await()
            Result(r.items.mapToList(), null)
        } catch (e: UserRecoverableAuthIOException) {
            Result(arrayListOf(), e)
        } catch (e: GooglePlayServicesAvailabilityIOException) {
            Result(arrayListOf(), e)
        } catch (e: IOException) {
            Result(arrayListOf(), e)
        }
    }

}