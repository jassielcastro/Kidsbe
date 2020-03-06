package com.ajcm.kidstube.ui.splash

import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState

sealed class UiSplash : UiState {
    object Loading : UiSplash()
    object LoadingError : UiSplash()
    object RequestAccount : UiSplash()
    object CheckPermissions : UiSplash()
    object Navigate : UiSplash()
}

sealed class ActionSplash : ActionState {
    object Start : ActionSplash()
    object RequestPermissions : ActionSplash()
    object ValidateAccount : ActionSplash()
    object StartSplash : ActionSplash()
    data class SaveAccount(val account: String) : ActionSplash()
    object LoadError : ActionSplash()
}