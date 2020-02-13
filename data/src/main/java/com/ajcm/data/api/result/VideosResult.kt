package com.ajcm.data.api.result

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosResult(
    val items: List<VideoItem>
): Parcelable

@Parcelize
data class VideoItem(
    @SerializedName("id")
    @Expose
    val videoId: VideoId,
    @SerializedName("snippet")
    @Expose
    val snippet: Snippet
): Parcelable

@Parcelize
data class VideoId(
    @SerializedName("videoId")
    @Expose
    val videoId: String
): Parcelable

@Parcelize
data class Snippet(
    @SerializedName("channelId")
    @Expose
    val channelId: String? = null,
    @SerializedName("title")
    @Expose
    val title: String? = null,
    @SerializedName("thumbnails")
    @Expose
    val thumbnails: Thumbnails? = null,
    @SerializedName("channelTitle")
    @Expose
    val channelTitle: String? = null
): Parcelable

@Parcelize
data class Thumbnails(
    @SerializedName("default")
    @Expose
    var default: Default? = null
): Parcelable

@Parcelize
data class Default(
    @SerializedName("url")
    @Expose
    var url: String? = null
): Parcelable