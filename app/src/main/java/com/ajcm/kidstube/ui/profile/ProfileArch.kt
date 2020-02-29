package com.ajcm.kidstube.ui.profile

import com.ajcm.domain.Avatar
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.UiState

typealias ItemAvatar = Pair<Avatar, Boolean>

sealed class UiProfile : UiState {
    data class UpdateUserInfo(val avatar: Avatar, val userName: String): UiProfile()
    data class AvatarContent(val avatarList: List<ItemAvatar>): UiProfile()
}

sealed class ActionProfile : ActionState {
    object Start: ActionProfile()
    object PrepareAvatarList: ActionProfile()
    data class AvatarSelected(val avatar: Avatar): ActionProfile()
}