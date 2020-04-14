package com.ajcm.kidstube

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class KidsTubeApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initDI()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

}