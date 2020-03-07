package com.ajcm.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserDb(
    @PrimaryKey(autoGenerate = false) val id: String,
    val userName: String,
    val userAvatar: String,
    val lastVideoWatched: String,
    val appEffect: Boolean = true,
    val soundEffect: Boolean = true,
    val allowMobileData: Boolean = false
)