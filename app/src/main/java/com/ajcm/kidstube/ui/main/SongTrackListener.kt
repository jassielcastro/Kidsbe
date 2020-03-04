package com.ajcm.kidstube.ui.main

import androidx.annotation.RawRes

interface SongTrackListener {
    /***
     * Enable when can play in onResume state
     */
    fun enableSong(enabled: Boolean)
    fun onPlaySong(@RawRes song: Int)
    fun onResumeSong()
    fun onPauseSong()
}