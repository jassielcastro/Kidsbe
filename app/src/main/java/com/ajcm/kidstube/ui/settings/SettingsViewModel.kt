package com.ajcm.kidstube.ui.settings

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class SettingsViewModel(private val localDB: LocalDataSource, uiDispatcher: CoroutineDispatcher): ScopedViewModel<UiSettings>(uiDispatcher) {

    override val model: LiveData<UiSettings>
        get() {
            if (mModel.value == null) dispatch(ActionSettings.Start)
            return mModel
        }

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        when(actionState) {
            ActionSettings.Start -> {
                launch {
                    consume(UiSettings.LoadUserInfo(localDB.getUser()))
                }
            }
        }
    }

}