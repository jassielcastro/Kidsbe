package com.ajcm.kidstube.ui.settings

import com.ajcm.domain.User
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState

sealed class UiSettings : UiState {
    data class LoadUserInfo(val user: User) : UiSettings()
    data class UpdateAppSongStatus(val user: User) : UiSettings()
}

sealed class ActionSettings : ActionState {
    object Start : ActionSettings()
    data class UpdateAppSoundsEnabled(val enabled: Boolean): ActionSettings()
    data class UpdateAppEffectEnabled(val enabled: Boolean): ActionSettings()
}