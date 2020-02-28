package com.ajcm.data.models

import android.os.Parcelable
import com.ajcm.data.models.VideoItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosResult constructor(
    val items: List<VideoItem>
): Parcelable








