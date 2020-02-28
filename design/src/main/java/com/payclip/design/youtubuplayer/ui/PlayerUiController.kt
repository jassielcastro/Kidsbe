package com.payclip.design.youtubuplayer.ui

interface PlayerUiController {

    fun showUi(show: Boolean): PlayerUiController
    fun showPlayPauseButton(show: Boolean): PlayerUiController

    fun showVideoTitle(show: Boolean): PlayerUiController
    fun setVideoTitle(videoTitle: String): PlayerUiController

    fun showCurrentTime(show: Boolean): PlayerUiController
    fun showDuration(show: Boolean): PlayerUiController

    fun showSeekBar(show: Boolean): PlayerUiController
    fun showBufferingProgress(show: Boolean): PlayerUiController

}