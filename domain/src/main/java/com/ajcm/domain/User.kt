package com.ajcm.domain

import java.io.Serializable

data class User(
    val userId: String,
    val userName: String,
    val userAvatar: Avatar = Avatar.MIA,
    val lastVideoWatched: String = "n0X3KG9D0rM",
    val appEffect: Boolean = true,
    val soundEffect: Boolean = true,
    val allowMobileData: Boolean = false
): Serializable