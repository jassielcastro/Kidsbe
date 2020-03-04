package com.ajcm.kidstube.ui.main

import android.os.Bundle
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.ajcm.kidstube.R
import com.ajcm.kidstube.common.SongHelper
import com.payclip.design.extensions.accelerateViews
import com.payclip.design.extensions.fullScreen

class MainActivity : AppCompatActivity(), SongTrackListener {

    private lateinit var navController: NavController

    private val songHelper: SongHelper by lazy {
        SongHelper()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        accelerateViews()

        navController = Navigation.findNavController(this, R.id.fragment)
    }

    override fun onPause() {
        onPauseSong()
        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            fullScreen()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    override fun enableSong(enabled: Boolean) {
        songHelper.setOnCanPlaySong(enabled)
    }

    override fun onResumeSong() {
        songHelper.onResume()
    }

    override fun onPauseSong() {
        songHelper.onPause()
    }

    override fun onPlaySong(@RawRes song: Int) {
        songHelper.onCreate(this, song)
    }
}
