package com.ajcm.data.api

import com.google.firebase.firestore.FirebaseFirestore

sealed class FirebaseApi {

    companion object Collections {
        const val MOST_POPULAR = "most_popular"
    }

    val db: FirebaseFirestore
        get() {
            return FirebaseFirestore.getInstance()
        }

    val url: String
        get() {
            return when (this) {
                Videos.List -> MOST_POPULAR
            }
        }

    sealed class Videos: FirebaseApi() {
        object List : Videos()
    }

}