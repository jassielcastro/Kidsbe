package com.ajcm.kidstube.ui.playvideo

import android.os.Bundle
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState
import com.ajcm.design.youtubuplayer.player.YouTubePlayer

sealed class UiPlayVideo : UiState {
    object Loading : UiPlayVideo()
    object LoadingError : UiPlayVideo()
    data class RenderYoutubePlayer(val youTubePlayer: YouTubePlayer) : UiPlayVideo()
    data class Content(val videos: List<Video>) : UiPlayVideo()
    data class ScrollToVideo(val position: Int) : UiPlayVideo()
    data class PlayVideo(val video: Video) : UiPlayVideo()
    data class RequestPermissions(val exception: Exception?) : UiPlayVideo()
}

sealed class ActionPlayVideo : ActionState {
    data class ObtainVideo(val bundle: Bundle?) : ActionPlayVideo()
    object Start : ActionPlayVideo()
    data class PlayerViewReady(val youTubePlayer: YouTubePlayer) : ActionPlayVideo()
    object Refresh : ActionPlayVideo()
    object LoadError : ActionPlayVideo()
    data class VideoSelected(val video: Video) : ActionPlayVideo()
    object PlayNextVideo : ActionPlayVideo()
    object ComputeScroll : ActionPlayVideo()
    object BlockCurrentVideo : ActionPlayVideo()
    object LoadVideosByCurrent : ActionPlayVideo()
}