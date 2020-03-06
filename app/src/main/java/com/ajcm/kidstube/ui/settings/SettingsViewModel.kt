package com.ajcm.kidstube.ui.settings

import androidx.lifecycle.LiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.common.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher

class SettingsViewModel(private val localDB: LocalDB, uiDispatcher: CoroutineDispatcher): ScopedViewModel<UiSettings>(uiDispatcher) {

    override val model: LiveData<UiSettings>
        get() {
            if (_model.value == null) dispatch(ActionSettings.Start)
            return _model
        }

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        when(actionState) {
            ActionSettings.Start -> {
                _model.value = UiSettings.LoadUserInfo(localDB.accountName, localDB.userAvatar)
            }
        }
    }

}