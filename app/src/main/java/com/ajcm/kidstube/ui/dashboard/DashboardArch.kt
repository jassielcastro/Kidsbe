package com.ajcm.kidstube.ui.dashboard

import com.ajcm.domain.Avatar
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.common.DashNav
import com.ajcm.kidstube.common.VideoAction

sealed class UiDashboard : UiState {
    object Loading : UiDashboard()
    data class LoadingError(val msg: String) : UiDashboard()
    data class UpdateUserInfo(val avatar: Avatar) : UiDashboard()
    object YoutubeStarted : UiDashboard()
    data class RequestPermissions(val exception: Exception?) : UiDashboard()
    data class Content(val videos: List<Video>) : UiDashboard()
    data class NavigateTo(val root: DashNav, val video: Video?, val userId: String) : UiDashboard()
}

sealed class ActionDashboard : ActionState {
    object Start : ActionDashboard()
    object StartYoutube : ActionDashboard()
    object Refresh : ActionDashboard()
    data class ParseException(val exception: Exception?) : ActionDashboard()
    data class LoadError(val msg: String) : ActionDashboard()
    data class VideoSelected(val videoAction: VideoAction) : ActionDashboard()
    data class ChangeRoot(val root: DashNav) : ActionDashboard()
}