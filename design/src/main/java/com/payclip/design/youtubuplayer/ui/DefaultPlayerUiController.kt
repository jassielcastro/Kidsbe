package com.payclip.design.youtubuplayer.ui

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.payclip.design.R
import com.payclip.design.extensions.toTime
import com.payclip.design.youtubuplayer.player.PlayerConstants
import com.payclip.design.youtubuplayer.player.YouTubePlayer
import com.payclip.design.youtubuplayer.player.listeners.YouTubePlayerListener
import com.payclip.design.youtubuplayer.player.listeners.YouTubePlayerSeekBarListener
import com.payclip.design.youtubuplayer.player.options.PanelState
import com.payclip.design.youtubuplayer.player.views.LegacyYouTubePlayerView
import com.payclip.design.youtubuplayer.ui.utils.FadeViewHelper

class DefaultPlayerUiController(youTubePlayerView: LegacyYouTubePlayerView, private val youTubePlayer: YouTubePlayer) : PlayerUiController,
    YouTubePlayerListener, YouTubePlayerSeekBarListener {

    /**
     * View used for for intercepting clicks and for drawing a black background.
     * Could have used controlsContainer, but in this way I'm able to hide all the control at once by hiding controlsContainer
     */
    private val panel: View

    private val controlsContainer: View

    private val videoTitle: TextView
    private val videoCurrentTime: TextView
    private val videoDurationView: TextView

    private val progressBar: ProgressBar
    private val playPauseButton: ImageView

    private val youtubePlayerSeekBar: SeekBar

    private val fadeControlsContainer: FadeViewHelper

    private var isPlaying = false
    private var isPlayPauseButtonEnabled = true

    private var videoDuration: Float = 0f

    private var onVideoEnded: () -> Unit = {}

    init {

        val controlsView = View.inflate(
            youTubePlayerView.context,
            R.layout.custom_player_ui,
            youTubePlayerView
        )

        panel = controlsView.findViewById(R.id.panel)
        controlsContainer = controlsView.findViewById(R.id.controls_container)

        videoTitle = controlsView.findViewById(R.id.videoTitle)
        videoCurrentTime = controlsView.findViewById(R.id.video_current_time)
        videoDurationView = controlsView.findViewById(R.id.video_duration)

        progressBar = controlsView.findViewById(R.id.progressbar)
        playPauseButton = controlsView.findViewById(R.id.play_pause_button)
        youtubePlayerSeekBar = controlsView.findViewById(R.id.seekBarTime)

        fadeControlsContainer = FadeViewHelper(controlsContainer)

        initClickListeners()
    }

    private fun initClickListeners() {
        youTubePlayer.addListener(this)
        youTubePlayer.addListener(fadeControlsContainer)

        panel.setOnClickListener {
            fadeControlsContainer.toggleVisibility()
        }
        playPauseButton.setOnClickListener { onPlayButtonPressed() }
    }

    fun setOnPanelClicked(clickedListener: (PanelState) -> Unit) {
        fadeControlsContainer.setOnPanelClicked(clickedListener)
    }

    fun setOnVideoFinishListener(finishListener: () -> Unit) {
        this.onVideoEnded = finishListener
    }

    override fun showVideoTitle(show: Boolean): PlayerUiController {
        videoTitle.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }

    override fun setVideoTitle(videoTitle: String): PlayerUiController {
        this.videoTitle.text = videoTitle
        return this
    }

    override fun showUi(show: Boolean): PlayerUiController {
        fadeControlsContainer.isDisabled = !show
        controlsContainer.visibility = if (show) View.VISIBLE else View.INVISIBLE
        return this
    }

    override fun showPlayPauseButton(show: Boolean): PlayerUiController {
        playPauseButton.visibility = if (show) View.VISIBLE else View.GONE

        isPlayPauseButtonEnabled = show
        return this
    }

    override fun showCurrentTime(show: Boolean): PlayerUiController {
        videoCurrentTime.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }

    override fun showDuration(show: Boolean): PlayerUiController {
        videoDurationView.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }

    override fun showSeekBar(show: Boolean): PlayerUiController {
        youtubePlayerSeekBar.visibility = if (show) View.VISIBLE else View.INVISIBLE
        return this
    }

    override fun showBufferingProgress(show: Boolean): PlayerUiController {
        progressBar.visibility = if (show) View.VISIBLE else View.INVISIBLE
        return this
    }

    private fun onPlayButtonPressed() {
        if (isPlaying)
            youTubePlayer.pause()
        else
            youTubePlayer.play()
    }

    private fun updateState(state: PlayerConstants.PlayerState) {
        when (state) {
            PlayerConstants.PlayerState.ENDED -> {
                onVideoEnded()
                isPlaying = false
            }
            PlayerConstants.PlayerState.PAUSED -> {
                isPlaying = false
            }
            PlayerConstants.PlayerState.PLAYING -> {
                isPlaying = true
            }

            else -> { }
        }

        updatePlayPauseButtonIcon(!isPlaying)
    }

    private fun updatePlayPauseButtonIcon(playing: Boolean) =
        playPauseButton.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)

    override fun seekTo(time: Float) = youTubePlayer.seekTo(time)

    // YouTubePlayer callbacks

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        updateState(state)

        if (state === PlayerConstants.PlayerState.PLAYING || state === PlayerConstants.PlayerState.PAUSED || state === PlayerConstants.PlayerState.VIDEO_CUED) {
            panel.setBackgroundColor(ContextCompat.getColor(panel.context, android.R.color.transparent))
            progressBar.visibility = View.GONE

            if (isPlayPauseButtonEnabled) playPauseButton.visibility = View.VISIBLE

            updatePlayPauseButtonIcon(state === PlayerConstants.PlayerState.PLAYING)

        } else {
            updatePlayPauseButtonIcon(false)

            if (state === PlayerConstants.PlayerState.BUFFERING) {
                progressBar.visibility = View.VISIBLE
                panel.setBackgroundColor(ContextCompat.getColor(panel.context, android.R.color.transparent))
                if (isPlayPauseButtonEnabled) playPauseButton.visibility = View.INVISIBLE
            }

            if (state === PlayerConstants.PlayerState.UNSTARTED) {
                progressBar.visibility = View.GONE
                if (isPlayPauseButtonEnabled) playPauseButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        videoCurrentTime.text = second.toTime()
        youtubePlayerSeekBar.progress = ((second * 100).toInt() / videoDuration).toInt()
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        videoDuration = duration
        videoDurationView.text = duration.toTime()
    }

    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {}
    override fun onReady(youTubePlayer: YouTubePlayer) {}
    override fun onPlaybackQualityChange(youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality) {}
    override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlayerConstants.PlaybackRate) {}
    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {}
    override fun onApiChange(youTubePlayer: YouTubePlayer) {}
    override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {}

}