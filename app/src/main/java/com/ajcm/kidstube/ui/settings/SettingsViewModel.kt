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
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val localDB: LocalDataSource,
    private val updateUser: UpdateUser,
    uiDispatcher: CoroutineDispatcher
) : ScopedViewModel<UiSettings>(uiDispatcher) {

    @InternalCoroutinesApi
    override val model: LiveData<UiSettings>
        get() {
            if (mModel.value == null) dispatch(ActionSettings.Start)
            return mModel
        }

    init {
        initScope()
    }

    @InternalCoroutinesApi
    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionSettings.Start -> {
                launch {
                    consume(UiSettings.LoadUserInfo(localDB.getUser()))
                }
            }
            is ActionSettings.UpdateAppSoundsEnabled -> {
                launch {
                    val newUser = localDB.getUser().copy(appEffect = actionState.enabled)
                    localDB.updateUser(newUser)
                    updateUser.invoke(newUser)
                    dispatch(ActionSettings.Start)
                }
            }
            is ActionSettings.UpdateAppEffectEnabled -> {
                launch {
                    val newUser = localDB.getUser().copy(soundEffect = actionState.enabled)
                    localDB.updateUser(newUser)
                    updateUser.invoke(newUser)
                }
            }
        }
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