package com.ajcm.design.youtubuplayer.player.utils

import com.ajcm.design.youtubuplayer.player.PlayerConstants
import com.ajcm.design.youtubuplayer.player.YouTubePlayer
import com.ajcm.design.youtubuplayer.player.listeners.AbstractYouTubePlayerListener

class YouTubePlayerTracker : AbstractYouTubePlayerListener() {

    /**
     * @return the player state. A value from [PlayerConstants.PlayerState]
     */
    var state: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN
        private set
    var currentSecond: Float = 0f
        private set
    var videoDuration: Float = 0f
        private set
    var videoId: String? = null
        private set

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        this.state = state
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        currentSecond = second
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        videoDuration = duration
    }

    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
        this.videoId = videoId
    }

}