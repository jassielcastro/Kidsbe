package com.ajcm.data.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Thumbnails(
    @SerializedName("default")
    @Expose
    var default: Default? = null
): Parcelable {

}