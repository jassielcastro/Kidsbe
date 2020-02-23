package com.ajcm.kidstube.ui.playvideo.customview

import android.view.View
import android.widget.*
import com.ajcm.kidstube.R
import com.ajcm.kidstube.extensions.hide
import com.ajcm.kidstube.extensions.toTime
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker

class CustomPlayerUiController constructor(
    private val playerUi: View,
    private val youTubePlayer: YouTubePlayer
) : AbstractYouTubePlayerListener(), YouTubePlayerListener {

    private lateinit var panel: View
    private lateinit var progressbar: ProgressBar
    private lateinit var seekBarTime: SeekBar
    private lateinit var videoCurrentTimeTextView: TextView
    private lateinit var videoDurationTextView: TextView

    private var videoDuration: Float = 0f

    private var playerTracker: YouTubePlayerTracker = YouTubePlayerTracker()

    init {
        youTubePlayer.addListener(playerTracker)

        initViews()
    }

    private fun initViews() {
        panel = playerUi.findViewById(R.id.panel)
        progressbar = playerUi.findViewById(R.id.progressbar)
        seekBarTime = playerUi.findViewById(R.id.seekBarTime)
        videoCurrentTimeTextView = playerUi.findViewById(R.id.video_current_time)
        videoDurationTextView = playerUi.findViewById(R.id.video_duration)
        val playPauseButton = playerUi.findViewById<ImageView>(R.id.play_pause_button)

        playPauseButton.setOnClickListener {
            when(playerTracker.state) {
                PlayerConstants.PlayerState.PLAYING -> {
                    youTubePlayer.pause()
                }
                else -> {
                    youTubePlayer.play()
                }
            }
        }
    }

    override fun onReady(youTubePlayer: YouTubePlayer) {
        super.onReady(youTubePlayer)
        progressbar.hide()
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        super.onStateChange(youTubePlayer, state)
        panel.alpha = 0f
        progressbar.hide()
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        super.onCurrentSecond(youTubePlayer, second)
        videoCurrentTimeTextView.text = second.toTime()
        seekBarTime.progress = ((second * 100) / videoDuration).toInt()
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        super.onVideoDuration(youTubePlayer, duration)
        videoDuration = duration
        videoDurationTextView.text = duration.toTime()
    }

}