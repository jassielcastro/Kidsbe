package com.ajcm.kidstube.ui.profile

import androidx.lifecycle.LiveData
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.common.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher

class ProfileViewModel(uiDispatcher: CoroutineDispatcher): ScopedViewModel<UiProfile>(uiDispatcher) {

    override val model: LiveData<UiProfile>
        get() {
            if (_model.value == null) dispatch(ActionProfile.None)
            return _model
        }

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {

    }

}