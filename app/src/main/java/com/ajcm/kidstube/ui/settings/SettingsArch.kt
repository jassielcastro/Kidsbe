package com.ajcm.kidstube.ui.settings

import com.ajcm.domain.Avatar
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState

sealed class UiSettings : UiState {
    data class LoadUserInfo(val userName: String, val avatar: Avatar) : UiSettings()
}

sealed class ActionSettings : ActionState {
    object Start : ActionSettings()
}