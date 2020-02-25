package com.ajcm.kidstube.ui.playvideo

import android.os.Bundle
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

sealed class UiPlayVideo : UiState {
    data class StartSharedVideo(val videoId: String) : UiPlayVideo()
    object Loading : UiPlayVideo()
    object LoadingError : UiPlayVideo()
    data class RenderYoutubePlayer(val youTubePlayer: YouTubePlayer) : UiPlayVideo()
    data class Content(val videos: List<Video>) : UiPlayVideo()
    data class PlayVideo(val videoId: String) : UiPlayVideo()
    data class RequestPermissions(val exception: Exception?) : UiPlayVideo()
}

sealed class ActionPlayVideo : ActionState {
    data class ObtainVideo(val bundle: Bundle?) : ActionPlayVideo()
    object StartYoutube : ActionPlayVideo()
    object Start : ActionPlayVideo()
    data class PlayerViewReady(val youTubePlayer: YouTubePlayer) : ActionPlayVideo()
    object Refresh : ActionPlayVideo()
    data class SaveLastVideoId(val lastVideoId: String) : ActionPlayVideo()
    object LoadError : ActionPlayVideo()
    data class VideoSelected(val video: Video) : ActionPlayVideo()
    object PlayNextVideo : ActionPlayVideo()
}