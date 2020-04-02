package com.ajcm.design.youtubuplayer.player.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NetworkListener : BroadcastReceiver() {

    var onNetworkUnavailable = { }
    var onNetworkAvailable = { }

    override fun onReceive(context: Context, intent: Intent) {
        if (WebUtils.isOnline(context))
            onNetworkAvailable()
        else
            onNetworkUnavailable()
    }
}