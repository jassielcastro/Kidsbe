package com.ajcm.data.api

import com.google.firebase.firestore.FirebaseFirestore

sealed class FirebaseApi {

    companion object Collections {
        const val USER_INFO = "user"
    }

    val db: FirebaseFirestore
        get() {
            return FirebaseFirestore.getInstance()
        }

    val url: String
        get() {
            return when (this) {
                User.Info -> USER_INFO
            }
        }

    sealed class User: FirebaseApi() {
        object Info : User()
    }

}