package com.ajcm.kidstube.ui.dashboard

import androidx.annotation.IdRes
import com.ajcm.domain.Avatar
import com.ajcm.domain.Video
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState

enum class DashNav(@IdRes val id: Int) {
    PROFILE(R.id.action_dashboardFragment_to_profileFragment),
    SETTINGS(R.id.action_dashboardFragment_to_settingsFragment),
    SEARCH(R.id.action_dashboardFragment_to_searchFragment),
    VIDEO(R.id.action_dashboardFragment_to_playVideoFragment)
}

sealed class UiDashboard : UiState {
    object Loading : UiDashboard()
    data class LoadingError(val msg: String) : UiDashboard()
    data class UpdateUserInfo(val avatar: Avatar) : UiDashboard()
    object YoutubeStarted : UiDashboard()
    data class RequestPermissions(val exception: Exception?) : UiDashboard()
    data class Content(val videos: List<Video>) : UiDashboard()
    data class NavigateTo(val root: DashNav, val video: Video?) : UiDashboard()
}

sealed class ActionDashboard : ActionState {
    object Start : ActionDashboard()
    object StartYoutube : ActionDashboard()
    object Refresh : ActionDashboard()
    data class ParseException(val exception: Exception?) : ActionDashboard()
    data class LoadError(val msg: String) : ActionDashboard()
    data class VideoSelected(val video: Video) : ActionDashboard()
    data class ChangeRoot(val root: DashNav) : ActionDashboard()
}