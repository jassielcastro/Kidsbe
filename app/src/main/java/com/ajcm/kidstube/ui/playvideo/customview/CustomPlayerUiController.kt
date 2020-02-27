package com.ajcm.kidstube.ui.playvideo.customview

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.ajcm.kidstube.R
import com.ajcm.kidstube.extensions.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker

class CustomPlayerUiController constructor(
    private val playerUi: View,
    private val youTubePlayer: YouTubePlayer,
    private val onPanelClicked: (PanelState) -> Unit,
    private val onVideoEnded: () -> Unit
) : AbstractYouTubePlayerListener(), YouTubePlayerListener {

    private lateinit var panel: View
    private lateinit var containerCustomAction: View
    private lateinit var progressbar: ProgressBar
    private lateinit var seekBarTime: SeekBar
    private lateinit var videoCurrentTimeTextView: TextView
    private lateinit var videoDurationTextView: TextView
    private lateinit var playPauseButton: ImageView

    private var videoDuration: Float = 0f

    private var playerTracker: YouTubePlayerTracker = YouTubePlayerTracker()

    private var lastState: PanelState = PanelState.COLLAPSED

    enum class PanelState {
        EXPAND, COLLAPSED
    }

    init {
        youTubePlayer.addListener(playerTracker)

        initViews()
    }

    private fun initViews() {
        panel = playerUi.findViewById(R.id.panel)
        containerCustomAction = playerUi.findViewById(R.id.containerCustomAction)
        progressbar = playerUi.findViewById(R.id.progressbar)
        seekBarTime = playerUi.findViewById(R.id.seekBarTime)
        videoCurrentTimeTextView = playerUi.findViewById(R.id.video_current_time)
        videoDurationTextView = playerUi.findViewById(R.id.video_duration)
        playPauseButton = playerUi.findViewById(R.id.play_pause_button)

        playPauseButton.setOnClickListener {
            when (playerTracker.state) {
                PlayerConstants.PlayerState.PLAYING -> {
                    youTubePlayer.pause()
                }
                else -> {
                    youTubePlayer.play()
                }
            }
        }

        panel.setOnClickListener {
            when (lastState) {
                PanelState.EXPAND -> {
                    showViewsControllerPlay()
                }
                else -> {
                    hideViewsControllerPlay()
                }
            }
        }
    }

    override fun onReady(youTubePlayer: YouTubePlayer) {
        super.onReady(youTubePlayer)
        panel.alpha = 0f
        progressbar.hide()
        hideViewsControllerPlay()
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        super.onStateChange(youTubePlayer, state)
        when (state) {
            PlayerConstants.PlayerState.ENDED -> {
                onVideoEnded()
                showViewsControllerPlay()
            }
            PlayerConstants.PlayerState.PAUSED -> {
                playPauseButton.loadRes(R.drawable.ic_play, false)
                showViewsControllerPlay()
            }
            PlayerConstants.PlayerState.PLAYING -> {
                playPauseButton.loadRes(R.drawable.ic_pause, false)
                hideViewsControllerPlay()
            }
            else -> { }
        }
    }

    private fun hideViewsControllerPlay() {
        delay(1800) {
            if (lastState == PanelState.COLLAPSED) {
                lastState = PanelState.EXPAND
                onPanelClicked(PanelState.EXPAND)
                containerCustomAction.hide()
            }
        }
    }

    private fun showViewsControllerPlay() {
        if (lastState == PanelState.EXPAND) {
            lastState = PanelState.COLLAPSED
            onPanelClicked(PanelState.COLLAPSED)
            containerCustomAction.show()
        }
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        super.onCurrentSecond(youTubePlayer, second)
        videoCurrentTimeTextView.text = second.toTime()
        seekBarTime.progress = ((second * 100).toInt() / videoDuration).toInt()
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        super.onVideoDuration(youTubePlayer, duration)
        videoDuration = duration
        videoDurationTextView.text = duration.toTime()
    }

}