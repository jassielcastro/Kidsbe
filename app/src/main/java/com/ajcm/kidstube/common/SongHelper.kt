package com.ajcm.kidstube.common

import android.animation.ValueAnimator
import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.payclip.design.extensions.delay

class SongHelper {

    private var mediaPlayer: MediaPlayer? = null

    private var canPlaySong: Boolean = false
    private var isReadyToPlay = false

    companion object {
        const val DEFAULT_VOLUME_SONG = 0.2f
        const val DEFAULT_VOLUME_DURATION = 820L
    }

    fun onCreate(context: Context, @RawRes song: Int) {
        if (mediaPlayer?.isPlaying == true) {
            setVolume(DEFAULT_VOLUME_SONG, 0f) {
                createSong(context, song)
            }
        } else {
            createSong(context, song)
        }
    }

    private fun createSong(context: Context, @RawRes song: Int) {
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(context, song)

        mediaPlayer?.setOnPreparedListener {
            isReadyToPlay = true
            onResume()
        }

        mediaPlayer?.setOnCompletionListener {
            it?.seekTo(0)
            onResume()
        }
    }

    fun setOnCanPlaySong(canPlaySong: Boolean) {
        this.canPlaySong = canPlaySong
    }

    fun onResume() {
        if (canPlaySong && isReadyToPlay && mediaPlayer?.isPlaying == false) {
            setVolume(0f, DEFAULT_VOLUME_SONG)
            mediaPlayer?.start()
        }
    }

    fun onPause() {
        if (mediaPlayer?.isPlaying == true) {
            setVolume(DEFAULT_VOLUME_SONG, 0f) {
                mediaPlayer?.pause()
            }
        }
    }

    fun hasSong(): Boolean = isReadyToPlay

    private fun setVolume(start: Float, end: Float, completion: () -> Unit = {}) {
        val valueAnimator = ValueAnimator.ofFloat(start, end)
        with(valueAnimator) {
            addUpdateListener {
                val value = it.animatedValue as Float
                mediaPlayer?.setVolume(value, value)
            }

            duration = DEFAULT_VOLUME_DURATION

            delay(DEFAULT_VOLUME_DURATION) {
                completion()
            }

            start()
        }
    }

}