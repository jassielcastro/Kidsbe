package com.ajcm.data.mappers

import com.ajcm.domain.Avatar
import com.ajcm.domain.User
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot?.toUser(): User? {
    return User(
        this?.id ?: "",
        if (this?.contains("userName") == true) this["userName"].toString() else "",
        if (this?.contains("userAvatar") == true) Avatar.valueOf(this["userAvatar"].toString()) else Avatar.MIA
    )
}