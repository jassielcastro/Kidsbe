package com.ajcm.data.source

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
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
        Log.i("YoutubeRemoteSource", "startYoutubeSession: $accountName")
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

    @SuppressLint("DefaultLocale")
    override suspend fun getPopularVideos(apiKey: String, relatedToVideoId: String): Result {
        return try {
           /*val list = youtube
                .setKey(apiKey)

            val resultList1 = getListOfVideosRelatedTo(relatedToVideoId, list).items
            val resultList2 = getListOfVideosRelatedTo(resultList1[0].id.videoId, list).items

            val completeList = (resultList1 + resultList2)
                .distinctBy { it.id.videoId }
                .filter {
                    !it.snippet.title.toLowerCase().contains("halloween") &&
                            !it.snippet.title.toLowerCase().contains("peppa") &&
                            !it.snippet.title.toLowerCase().contains("carcel") &&
                            !it.snippet.title.toLowerCase().contains("bruja") &&
                            !it.snippet.title.toLowerCase().contains("witch") &&
                            !it.snippet.title.toLowerCase().contains("calavera") &&
                            !it.snippet.title.toLowerCase().contains("muerto") &&
                            !it.snippet.title.toLowerCase().contains("fantasma") &&
                            !it.snippet.title.toLowerCase().contains("ghost") &&
                            !it.snippet.title.toLowerCase().contains("susto") &&
                            !it.snippet.title.toLowerCase().contains("scare") &&
                            !it.snippet.title.toLowerCase().contains("mounstro") &&
                            !it.snippet.title.toLowerCase().contains("monster") &&
                            !it.snippet.title.toLowerCase().contains("zombie") &&
                            !it.snippet.title.toLowerCase().contains("apocalypse") &&
                            !it.snippet.title.toLowerCase().contains("miedo")
                }

            Result(completeList.mapToList(), null)*/
            getFakeVideoList()
        } catch (e: IOException) {
            Result(arrayListOf(), e)
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