package com.ajcm.kidstube.ui.search

import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.common.DashNav
import com.ajcm.kidstube.common.VideoAction

sealed class UiSearch : UiState {
    object Start : UiSearch()
    data class Content(val videos: List<Video>) : UiSearch()
    object Loading: UiSearch()
    object LoadError: UiSearch()
    data class NavigateTo(val root: DashNav, val video: Video?, val userId: String) : UiSearch()
}

sealed class ActionSearch : ActionState {
    object PrepareYoutube : ActionSearch()
    data class Search(val byText: String?) : ActionSearch()
    data class VideoSelected(val videoAction: VideoAction) : ActionSearch()
}