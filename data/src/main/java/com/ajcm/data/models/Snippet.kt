package com.ajcm.data.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

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
): Parcelable {

}