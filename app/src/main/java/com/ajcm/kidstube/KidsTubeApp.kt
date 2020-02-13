package com.ajcm.kidstube

import android.app.Application

class KidsTubeApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initDI()
    }

}