package com.ajcm.kidstube.ui.settings

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.domain.User
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.kidstube.ui.main.SongTrackListener
import com.ajcm.usecases.UpdateUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val localDB: LocalDataSource<User>,
    private val updateUser: UpdateUser,
    uiDispatcher: CoroutineDispatcher
) : ScopedViewModel<UiSettings>(uiDispatcher) {

    override val model: LiveData<UiSettings>
        get() {
            if (mModel.value == null) dispatch(ActionSettings.Start)
            return mModel
        }

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionSettings.Start -> {
                launch {
                    consume(UiSettings.LoadUserInfo(localDB.getObject()))
                }
            }
            is ActionSettings.UpdateAppSoundsEnabled -> {
                launch {
                    val newUser = localDB.getObject().copy(appEffect = actionState.enabled)
                    updateUser(newUser)
                    consume(UiSettings.UpdateAppSongStatus(newUser))
                }
            }
            is ActionSettings.UpdateAppEffectEnabled -> {
                launch {
                    val newUser = localDB.getObject().copy(soundEffect = actionState.enabled)
                    updateUser(newUser)
                }
            }
            is ActionSettings.UpdateMobileDataEnabled -> {
                launch {
                    val newUser = localDB.getObject().copy(allowMobileData = actionState.enabled)
                    updateUser(newUser)
                }
            }
        }
    }

    private suspend fun updateUser(user: User) {
        localDB.update(user)
        updateUser.invoke(user)
    }

    fun onHandleMusicApp(user: User, listener: SongTrackListener?) {
        launch {
            if (user.appEffect) {
                if (listener?.hasSong() == false) {
                    listener.onPlaySong(R.raw.dashboard_ben_smile)
                } else {
                    listener?.onResumeSong()
                }
            } else {
                listener?.onPauseSong()
            }
        }
    }

}