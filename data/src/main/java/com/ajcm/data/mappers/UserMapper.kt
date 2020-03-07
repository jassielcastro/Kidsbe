package com.ajcm.data.mappers

import com.ajcm.data.common.Constants
import com.ajcm.data.models.UserDb
import com.ajcm.domain.Avatar
import com.ajcm.domain.User
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot?.toUser(): User? {
    return User(
        this?.id ?: "",
        if (this?.contains("userName") == true) this["userName"].toString() else "",
        if (this?.contains("userAvatar") == true) Avatar.valueOf(this["userAvatar"].toString()) else Avatar.MIA,
        if (this?.contains("lastVideoWatched") == true) this["lastVideoWatched"].toString() else Constants.DEFAULT_LAST_VIDEO_ID,
        this?.getBoolean("appEffect") ?: true,
        this?.getBoolean("soundEffect") ?: true,
        this?.getBoolean("allowMobileData") ?: false
    )
}

fun User.toRoomUser(): UserDb =
    UserDb(
        userId,
        userName,
        userAvatar.name,
        lastVideoWatched,
        appEffect,
        soundEffect,
        allowMobileData
    )

fun UserDb.toUser(): User =
    User(
        id,
        userName,
        Avatar.valueOf(userAvatar),
        lastVideoWatched,
        appEffect,
        soundEffect,
        allowMobileData
    )