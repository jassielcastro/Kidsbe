package com.ajcm.kidstube.ui.splash

import androidx.lifecycle.LiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.domain.Avatar
import com.ajcm.domain.User
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.common.ScopedViewModel
import com.ajcm.usecases.SaveUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val localDB: LocalDB,
    private val saveUser: SaveUser,
    uiDispatcher: CoroutineDispatcher
) : ScopedViewModel<UiSplash>(uiDispatcher) {

    @InternalCoroutinesApi
    override val model: LiveData<UiSplash>
        get() {
            if (_model.value == null) dispatch(ActionSplash.ValidateAccount)
            return _model
        }

    init {
        initScope()
    }

    @InternalCoroutinesApi
    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionSplash.ValidateAccount -> {
                checkAccountName()
            }
            ActionSplash.StartSplash -> {
                playSplash()
            }
            is ActionSplash.SaveAccount -> {
                saveAccountName(actionState.account)
            }
            ActionSplash.LoadError -> {
                loadError()
            }
        }
    }

    private fun checkAccountName() {
        _model.value = UiSplash.Loading
        if (localDB.accountName.isEmpty()) {
            _model.value = UiSplash.RequestAccount
        } else {
            playSplash()
        }
    }

    private fun playSplash() = launch {
        _model.value = UiSplash.Loading
        delay(5150)
        _model.value = UiSplash.Navigate
    }

    @InternalCoroutinesApi
    private fun saveAccountName(accountName: String) {
        localDB.accountName = accountName
        launch {
            saveUser.invoke(User("", accountName, Avatar.MIA))?.let {
                localDB.userId = it
            }
        }
        checkAccountName()
    }

    private fun loadError() {
        _model.value = UiSplash.LoadingError
    }

}