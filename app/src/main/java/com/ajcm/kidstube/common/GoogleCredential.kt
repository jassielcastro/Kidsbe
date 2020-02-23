package com.ajcm.kidstube.common

import android.content.Context
import com.ajcm.data.common.Constants
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff

class GoogleCredential(context: Context) {

    val credential: GoogleAccountCredential by lazy {
        GoogleAccountCredential
            .usingOAuth2(context, Constants.SCOPES)
            .setBackOff(ExponentialBackOff())
    }

}