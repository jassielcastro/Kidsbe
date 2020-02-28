package com.ajcm.domain

import java.io.Serializable

data class User(
    val userId: String,
    val userName: String,
    val userAvatar: Avatar
): Serializable