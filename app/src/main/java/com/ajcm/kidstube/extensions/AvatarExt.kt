package com.ajcm.kidstube.extensions

import com.ajcm.domain.Avatar
import com.ajcm.kidstube.R

fun Avatar.getDrawable(): Int = when (this) {
    Avatar.MIA -> R.drawable.bck_avatar_kids_mia
    Avatar.CRUNCHY -> R.drawable.bck_avatar_kids_crunchy
    Avatar.EMOHORSE -> R.drawable.bck_avatar_kids_emohorse
    Avatar.FLASHMEO -> R.drawable.bck_avatar_kids_flashmeow
    Avatar.GUMI -> R.drawable.bck_avatar_kids_gumi
    Avatar.MR_MARSHMELLOW -> R.drawable.bck_avatar_kids_mrmarshmellow
    Avatar.OPTIMIST_GIRAFFE -> R.drawable.bck_avatar_kids_optimisticgiraffe
    Avatar.BURNED_TOAST -> R.drawable.bck_avatar_kids_burnedtoast
    Avatar.FAILNANA -> R.drawable.bck_avatar_kids_failnana
    Avatar.KEVIN -> R.drawable.bck_avatar_kids_kevin
    Avatar.NICE_CREAMS -> R.drawable.bck_avatar_kids_nicecreams
    Avatar.NINJA_MARBLE -> R.drawable.bck_avatar_kids_ninjamarble
    Avatar.SHY_PANDA -> R.drawable.bck_avatar_kids_shypanda
}