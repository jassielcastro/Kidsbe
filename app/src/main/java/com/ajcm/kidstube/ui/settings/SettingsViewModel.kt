package com.ajcm.kidstube.ui.settings

import androidx.lifecycle.LiveData
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.common.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher

class SettingsViewModel(uiDispatcher: CoroutineDispatcher): ScopedViewModel<UiSettings>(uiDispatcher) {

    override val model: LiveData<UiSettings>
        get() {
            if (_model.value == null) dispatch(ActionSettings.None)
            return _model
        }

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {

    }

}