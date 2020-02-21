package com.ajcm.data.api

import com.ajcm.data.api.result.VideosResult
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YoutubeService {

    @GET("search?part=id,snippet")
    fun listVideosAsync(
        @Query("api_key") apiKey: String,
        @Query("relatedToVideoId") relatedToVideoId: String,
        @Query("maxResults") maxResults: Int = 25,
        @Query("safeSearch") safeSearch: String = "strict",
        @Query("type") type: String = "video",
        @Query("fields") fields: String = "items(id/kind,id/videoId,snippet/channelId,snippet/title,snippet/thumbnails/default/url,snippet/channelTitle)"
    ): Deferred<VideosResult>

    @GET("search?part=id,snippet")
    fun searchVideosAsync(
        @Query("api_key") apiKey: String,
        @Query("q") byText: String,
        @Query("maxResults") maxResults: Int = 25,
        @Query("safeSearch") safeSearch: String = "strict",
        @Query("type") type: String = "video",
        @Query("fields") fields: String = "items(id/kind,id/videoId,snippet/channelId,snippet/title,snippet/thumbnails/default/url,snippet/channelTitle)"
    ): Deferred<VideosResult>

}