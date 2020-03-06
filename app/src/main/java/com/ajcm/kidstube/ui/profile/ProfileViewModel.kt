package com.ajcm.kidstube.ui.profile

import androidx.lifecycle.LiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.domain.Avatar
import com.ajcm.domain.User
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.usecases.UpdateUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class ProfileViewModel(private val localDB: LocalDB, private val updateUser: UpdateUser, uiDispatcher: CoroutineDispatcher) :
    ScopedViewModel<UiProfile>(uiDispatcher) {

    @InternalCoroutinesApi
    override val model: LiveData<UiProfile>
        get() {
            if (mModel.value == null) dispatch(ActionProfile.Start)
            return mModel
        }

    init {
        initScope()
    }

    @InternalCoroutinesApi
    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionProfile.Start -> {
                consume(UiProfile.UpdateUserInfo(localDB.userAvatar, localDB.accountName))
            }
            ActionProfile.PrepareAvatarList -> {
                consume(UiProfile.AvatarContent(
                    Avatar.values().asList().map {
                        ItemAvatar(it, localDB.userAvatar == it)
                    }
                ))
            }
            is ActionProfile.AvatarSelected -> {
                updateAvatarProfile(actionState.avatar)
            }
        }
    }

    @InternalCoroutinesApi
    private fun updateAvatarProfile(avatar: Avatar) {
        launch {
            updateUser.invoke(User(localDB.userId, localDB.accountName, avatar)).let {
                if (it) {
                    localDB.userAvatar = avatar
                    dispatch(ActionProfile.Start)
                }
            }
        }
    }

}