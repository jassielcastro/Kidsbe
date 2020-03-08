package com.ajcm.kidstube.ui.profile

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.domain.Avatar
import com.ajcm.domain.User
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.usecases.UpdateUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ProfileViewModel(private val localDB: LocalDataSource<User>, private val updateUser: UpdateUser, uiDispatcher: CoroutineDispatcher) :
    ScopedViewModel<UiProfile>(uiDispatcher) {

    override val model: LiveData<UiProfile>
        get() {
            if (mModel.value == null) dispatch(ActionProfile.Start)
            return mModel
        }

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionProfile.Start -> {
                launch {
                    consume(UiProfile.UpdateUserInfo(localDB.getObject()))
                }
            }
            ActionProfile.PrepareAvatarList -> {
                launch {
                    getUser().let {
                        val avatarList = Avatar.values().asList().map { avatar ->
                            ItemAvatar(avatar, it.userAvatar == avatar)
                        }
                        consume(UiProfile.AvatarContent(avatarList))
                    }
                }
            }
            is ActionProfile.AvatarSelected -> {
                updateAvatarProfile(actionState.avatar)
            }
        }
    }

    private fun updateAvatarProfile(avatar: Avatar) {
        launch {
            val newUser = getUser().copy(userAvatar = avatar)
            updateUser.invoke(newUser).let {
                if (it) {
                    localDB.update(newUser)
                    dispatch(ActionProfile.Start)
                }
            }
        }
    }

    private suspend fun getUser(): User = suspendCancellableCoroutine { continuation ->
        launch {
            continuation.resume(localDB.getObject())
        }
    }

}