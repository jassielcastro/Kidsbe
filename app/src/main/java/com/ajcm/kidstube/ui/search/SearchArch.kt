package com.ajcm.kidstube.ui.search

import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState

sealed class UiSearch : UiState {
object None: UiSearch()
}

sealed class ActionSearch : ActionState {
object None: ActionSearch()
}