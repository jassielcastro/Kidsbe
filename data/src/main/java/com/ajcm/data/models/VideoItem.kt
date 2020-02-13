package com.ajcm.data.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoItem(
    @SerializedName("id")
    @Expose
    val videoId: VideoId,
    @SerializedName("snippet")
    @Expose
    val snippet: Snippet
): Parcelable {

}