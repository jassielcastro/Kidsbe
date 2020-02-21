package com.ajcm.data.auth

import com.google.android.gms.common.Scopes
import com.google.api.services.youtube.YouTubeScopes

object Constants {

    val SCOPES = arrayListOf(Scopes.PROFILE, YouTubeScopes.YOUTUBE)
    const val APP_NAME = "KidsTube"
    const val REQUEST_ACCOUNT_PICKER = 1217
    const val AUTH_CODE_REQUEST_CODE = 1802
    const val REQUEST_GOOGLE_PLAY_SERVICES = 1109

}