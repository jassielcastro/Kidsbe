package com.ajcm.kidstube.ui.settings

import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState

sealed class UiSettings : UiState {
object None: UiSettings()
}

sealed class ActionSettings : ActionState {
object None: ActionSettings()
}