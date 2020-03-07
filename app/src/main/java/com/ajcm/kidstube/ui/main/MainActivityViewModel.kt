package com.ajcm.kidstube.ui.main

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher

class MainActivityViewModel(
    private val localDB: LocalDataSource,
    uiDispatcher: CoroutineDispatcher
) :
    ScopedViewModel<UiMain>(uiDispatcher) {

    override val model: LiveData<UiMain>
        get() {
            if (mModel.value == null) dispatch(ActionMain.Start)
            return mModel
        }

    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionMain.Start -> {
                consume(UiMain.ChackSounds)
            }
        }
    }

    suspend fun isAppSoundsEnabled(): Boolean {
        return if (localDB.existUser()) localDB.getUser().appEffect else true
    }

    suspend fun isAppEffectsEnabled(): Boolean {
        return if (localDB.existUser()) localDB.getUser().soundEffect else true
    }

}