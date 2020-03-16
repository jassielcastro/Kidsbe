package com.ajcm.data.source

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.ajcm.data.api.FirebaseApi
import com.ajcm.data.api.GoogleSession
import com.ajcm.data.common.Constants
import com.ajcm.data.mappers.mapToList
import com.ajcm.data.models.Result
import com.ajcm.domain.Video
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume

class YoutubeRemoteSource(private val application: Application, private val api: FirebaseApi): RemoteDataSource {

    private lateinit var youtube: YouTube.Search.List

    private val tempBlackListVideos: List<String> by lazy {
        listOf(
            "halloween",
            "death",
            "muerte",
            "muerto",
            "peppa",
            "carcel",
            "bruja",
            "witch",
            "calavera",
            "muerto",
            "fantasma",
            "ghost",
            "susto",
            "scare",
            "mounstro",
            "mounstruo",
            "monster",
            "zombie",
            "apocalypse",
            "terror",
            "terrible",
            "scary",
            "spooky",
            "miedo"
        )
    }

    suspend fun startYoutubeSession(accountName: String) = withContext(Dispatchers.IO) {
        Log.i("YoutubeRemoteSource", "startYoutubeSession: $accountName")
        youtube = GoogleSession(application, accountName)
            .getYoutubeSession()
            .Search()
            .list("id,snippet")
            .setType("video")
            .setMaxResults(Constants.DEFAULT_VIDEO_COUNT)
            .setSafeSearch("strict")
            .setVideoDuration("medium")
            .setFields("items(id/kind,id/videoId,snippet/channelId,snippet/title,snippet/thumbnails/high/url,snippet/channelTitle)")
    }

    override suspend fun searchVideos(apiKey: String, title: String): Result {
        return try {
            val list = youtube
                .setKey(apiKey)
                .setQ("$title|videos para niños")

            val request = withContext(Dispatchers.IO) {
                async {
                    list.execute()
                }
            }
            val resultList1 = request.await().items
            val list1Filtered = filterVideoList(resultList1, tempBlackListVideos)
            val mappedVideos = list1Filtered.mapToList()
            withContext(Dispatchers.IO) {
                saveVideos(mappedVideos)
            }
            Result(mappedVideos, null)
        } catch (e: IOException) {
            Result(getVideosSaved(), e)
        }
    }

    override suspend fun getPopularVideos(apiKey: String, relatedToVideoId: String): Result {
        return try {
           val list = youtube
                .setKey(apiKey)

            val resultList1 = getListOfVideosRelatedTo(relatedToVideoId, list).items
            val list1Filtered = filterVideoList(resultList1, tempBlackListVideos)

            val resultList2 = getListOfVideosRelatedTo(list1Filtered[0].id.videoId, list).items
            val list2Filtered = filterVideoList(resultList2, tempBlackListVideos)

            val completeList = (list1Filtered + list2Filtered)
                .distinctBy { it.id.videoId }

            val mappedVideos = completeList.mapToList()
            withContext(Dispatchers.IO) {
                saveVideos(mappedVideos)
            }
            Result(mappedVideos, null)
        } catch (e: IOException) {
            Result(getVideosSaved(), e)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filterVideoList(list: List<SearchResult>, blackList: List<String>): List<SearchResult> {
        return list.filter { video ->
            blackList.none {
                video.snippet.title.toLowerCase().contains(it)
            }
        }
    }

    @Throws(IOException::class)
    private suspend fun getListOfVideosRelatedTo(id: String, list: YouTube.Search.List) : SearchListResponse {
        val request = withContext(Dispatchers.IO) {
            async {
                list.relatedToVideoId = id
                list.execute()
            }
        }
        return request.await()
    }

    private suspend fun getVideosSaved(): List<Video> = suspendCancellableCoroutine { continuation ->
        api.db
            .collection(api.url)
            .limit(100)
            .get()
            .addOnCompleteListener {
                continuation.resume(it.result?.toObjects(Video::class.java)?.toList() ?: emptyList())
            }
    }

    private fun saveVideos(videos: List<Video>) {
        videos.map { value ->
            api.db
                .collection(api.url)
                .document(value.videoId)
                .set(
                    mapOf(
                        "title" to value.title,
                        "thumbnail" to value.thumbnail,
                        "channelId" to value.channelId,
                        "channelTitle" to value.channelTitle
                    )
                )
        }
    }

}