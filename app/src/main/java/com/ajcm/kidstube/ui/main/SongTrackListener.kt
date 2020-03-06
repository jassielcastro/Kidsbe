package com.ajcm.kidstube.ui.main

import androidx.annotation.RawRes

interface SongTrackListener {
    /***
     * Enable when can play in onResume state
     */
    suspend fun enableSong(enabled: Boolean)
    suspend fun onPlaySong(@RawRes song: Int)
    suspend fun onResumeSong()
    fun onPauseSong()
    fun hasSong(): Boolean
}