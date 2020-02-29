package com.ajcm.data.source

import android.app.Application
import com.ajcm.data.auth.Session
import com.ajcm.data.common.Constants
import com.ajcm.data.mappers.mapToList
import com.ajcm.data.models.Result
import com.ajcm.domain.Video
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
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
            .setMaxResults(Constants.DEFAULT_VIDEO_COUNT)
            .setSafeSearch("strict")
            .setVideoDuration("medium")
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
        } catch (e: IOException) {
            Result(arrayListOf(), e)
        }
    }

    override suspend fun getPopularVideos(apiKey: String, relatedToVideoId: String): Result {
        return try {
            /*val list = youtube
                .setKey(apiKey)

            val resultList1 = getListOfVideosRelatedTo(relatedToVideoId, list).items
            val resultList2 = getListOfVideosRelatedTo(resultList1[0].id.videoId, list).items

            val completeList = (resultList1 + resultList2).distinctBy { it.id.videoId }

            Result(completeList.mapToList(), null)*/
            getFakeVideoList()
        } catch (e: IOException) {
            Result(arrayListOf(), e)
        }
    }

    private suspend fun getListOfVideosRelatedTo(id: String, list: YouTube.Search.List): SearchListResponse {
        val request = withContext(Dispatchers.IO) {
            async {
                list.relatedToVideoId = id
                list.execute()
            }
        }
        return request.await()
    }

    private fun getFakeVideoList() : Result {
        return Result(arrayListOf(
            Video("4QOnCD34gZc", "", "", "https://i.ytimg.com/vi/4QOnCD34gZc/hqdefault.jpg?sqp=-oaymwEXCNACELwBSFryq4qpAwkIARUAAIhCGAE=&rs=AOn4CLAKAcgpkwvdxABmu2J9c2stNIJTrA", ""),
            Video("1enTGxhzQJI", "", "", "https://i.ytimg.com/vi/kEqWsBcKJ_A/hqdefault.jpg?sqp=-oaymwEXCNACELwBSFryq4qpAwkIARUAAIhCGAE=&rs=AOn4CLAIVtsGX5Uav80eEyxv3L2AF2YLLQ", "")
        ), null)
    }

}