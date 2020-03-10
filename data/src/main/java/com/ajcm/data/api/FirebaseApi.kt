package com.ajcm.data.api

import com.google.firebase.firestore.FirebaseFirestore

sealed class FirebaseApi {

    companion object Collections {
        const val USER_INFO = "user"
        const val VIDEO_WATCHED = "videos_watched"
    }

    val db: FirebaseFirestore
        get() {
            return FirebaseFirestore.getInstance()
        }

    val url: String
        get() {
            return when (this) {
                User.Info -> USER_INFO
                is User.VideoWatched -> "$USER_INFO/${this.userId}/$VIDEO_WATCHED"
            }
        }

    sealed class User: FirebaseApi() {
        object Info : User()
        data class VideoWatched(val userId: String) : User()
    }

}