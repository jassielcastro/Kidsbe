package com.ajcm.kidstube.ui.main

import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState

sealed class UiMain : UiState {
    object ChackSounds: UiMain()
}

sealed class ActionMain : ActionState {
    object Start: ActionMain()
}