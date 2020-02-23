package com.ajcm.kidstube.ui.profile

import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState

sealed class UiProfile : UiState {
    object None: UiProfile()
}

sealed class ActionProfile : ActionState {
    object None: ActionProfile()
}