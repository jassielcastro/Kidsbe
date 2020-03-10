package com.ajcm.data.source

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
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
import kotlinx.coroutines.withContext
import java.io.IOException

class YoutubeRemoteSource(private val application: Application): RemoteDataSource {

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
            "miedo"
        )
    }

    fun startYoutubeSession(accountName: String) {
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
           val list = youtube
                .setKey(apiKey)

            val resultList1 = getListOfVideosRelatedTo(relatedToVideoId, list).items
            val list1Filtered = filterVideoList(resultList1, tempBlackListVideos)

            val resultList2 = getListOfVideosRelatedTo(list1Filtered[0].id.videoId, list).items
            val list2Filtered = filterVideoList(resultList2, tempBlackListVideos)

            val completeList = (list1Filtered + list2Filtered)
                .distinctBy { it.id.videoId }

            Result(completeList.mapToList(), null)
            //getFakeVideoList()
        } catch (e: IOException) {
            Result(arrayListOf(), e)
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

    private fun getFakeVideoList() : Result {
        return Result(arrayListOf(
            Video("92WXff7v_ZI", "", "Paw Patrol | Tareas Educacionales - parte 1 \uD83D\uDC36 | Nick Jr.", "https://i.ytimg.com/vi/6y1PA2BEr9o/hqdefault.jpg?sqp=-oaymwEXCNACELwBSFryq4qpAwkIARUAAIhCGAE=&rs=AOn4CLDqEcYL52qHxRKrGo8Hi7OMKNSupg", ""),
            Video("sjbqijw7FXE", "", "¿Dónde está Chicky? | Dibujos Animados Para Niños | Compilación De Dibujos Animados #224", "https://i.ytimg.com/vi/sjbqijw7FXE/hqdefault.jpg?sqp=-oaymwEXCNACELwBSFryq4qpAwkIARUAAIhCGAE=&rs=AOn4CLBxXkfknqeQ5Z6ZbZTDKxm3RjvPXg", ""),
            Video("C-Pav2Y3UTQ", "", "Leo el Pequeño Camión - Los mejores capítulos en español", "https://i.ytimg.com/vi/C-Pav2Y3UTQ/hqdefault.jpg?sqp=-oaymwEXCNACELwBSFryq4qpAwkIARUAAIhCGAE=&rs=AOn4CLBAtnYf8WzO7UpMzbH5-MRk_QFSUA", ""),
            Video("AKbDeMISmWM", "", "POCOYÓ en ESPAÑOL - Pocoyo en el País de las Maravillas [131 min] | CARICATURAS y DIBUJOS ANIMADOS", "https://i.ytimg.com/vi/AKbDeMISmWM/hqdefault.jpg?sqp=-oaymwEXCNACELwBSFryq4qpAwkIARUAAIhCGAE=&rs=AOn4CLBv3zHS3Ahz4s2qZWvyG7Vs9_enaQ", ""),
            Video("De1od9ggPPE", "", "\uD83E\uDD29 TODOS LOS EPISODIOS DE \uD83D\uDE32 BEBÉS LLORONES \uD83D\uDCA7 LÁGRIMAS MÁGICAS \uD83D\uDC95", "https://i.ytimg.com/vi/De1od9ggPPE/hqdefault.jpg?sqp=-oaymwEXCNACELwBSFryq4qpAwkIARUAAIhCGAE=&rs=AOn4CLA81xRWHc8K1IoyEMxTQaNMR27Ycg", "")
        ), null)
    }

}