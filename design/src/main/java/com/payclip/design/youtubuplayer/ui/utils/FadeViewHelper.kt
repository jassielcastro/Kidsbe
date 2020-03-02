package com.payclip.design.youtubuplayer.ui.utils

import android.animation.Animator
import android.view.View
import com.payclip.design.youtubuplayer.player.PlayerConstants
import com.payclip.design.youtubuplayer.player.YouTubePlayer
import com.payclip.design.youtubuplayer.player.listeners.YouTubePlayerListener
import com.payclip.design.youtubuplayer.player.options.PanelState

class FadeViewHelper(val targetView: View): YouTubePlayerListener {
    companion object {
        const val  DEFAULT_ANIMATION_DURATION = 300L
        const val  DEFAULT_FADE_OUT_DELAY = 3000L
    }

    private var isPlaying = false

    private var canFade = false
    private var isVisible = true

    private var fadeOut: Runnable = Runnable{ fade(0f) }

    var isDisabled = false

    /**
     * Duration of the fade animation in milliseconds.
     */
    private var animationDuration = DEFAULT_ANIMATION_DURATION

    /**
     * Delay after which the view automatically fades out.
     */
    private var fadeOutDelay = DEFAULT_FADE_OUT_DELAY

    private var listener: (PanelState) -> Unit = {}
    private var panelYoutube: View? = null

    fun toggleVisibility() {
        fade(if (isVisible) 0f else 1f)
    }

    fun onResume() {
        isPlaying = true
        toggleVisibility()
        isPlaying = false
    }

    private fun fade(finalAlpha: Float) {
        if (!canFade || isDisabled)
            return

        isVisible = finalAlpha != 0f

        // if the controls are shown and the player is playing they should automatically fade after a while.
        // otherwise don't do anything automatically
        if (finalAlpha == 1f && isPlaying)
            targetView.handler?.postDelayed(fadeOut, fadeOutDelay)
        else
            targetView.handler?.removeCallbacks(fadeOut)

        targetView.animate()
            .alpha(finalAlpha)
            .setDuration(animationDuration)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                    if (finalAlpha == 1f) {
                        targetView.visibility = View.VISIBLE
                        listener(PanelState.COLLAPSED)
                        panelYoutube?.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animator: Animator) {
                    if (finalAlpha == 0f) {
                        targetView.visibility = View.GONE
                        listener(PanelState.EXPAND)
                        panelYoutube?.visibility = View.GONE
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            }).start()
    }

    private fun updateState(state: PlayerConstants.PlayerState) {
        when (state) {
            PlayerConstants.PlayerState.ENDED -> isPlaying = false
            PlayerConstants.PlayerState.PAUSED -> isPlaying = false
            PlayerConstants.PlayerState.PLAYING -> isPlaying = true
            PlayerConstants.PlayerState.UNSTARTED -> { }
            else -> { }
        }
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        updateState(state)

        when(state) {
            PlayerConstants.PlayerState.PLAYING, PlayerConstants.PlayerState.PAUSED, PlayerConstants.PlayerState.VIDEO_CUED -> {
                canFade = true
                if (state == PlayerConstants.PlayerState.PLAYING)
                    targetView.handler?.postDelayed(fadeOut, fadeOutDelay)
                else
                    targetView.handler?.removeCallbacks(fadeOut)
            }
            PlayerConstants.PlayerState.BUFFERING, PlayerConstants.PlayerState.UNSTARTED -> {
                fade(1f)
                canFade = false
            }
            PlayerConstants.PlayerState.UNKNOWN -> fade(1f)
            PlayerConstants.PlayerState.ENDED -> fade(1f)
        }
    }

    fun setOnPanelListener(listener: (PanelState) -> Unit, panelYoutube: View) {
        this.listener = listener
        this.panelYoutube = panelYoutube
    }

    override fun onReady(youTubePlayer: YouTubePlayer) { }
    override fun onPlaybackQualityChange(youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality) { }
    override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlayerConstants.PlaybackRate) { }
    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) { }
    override fun onApiChange(youTubePlayer: YouTubePlayer) { }
    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) { }
    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) { }
    override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) { }
    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) { }
}