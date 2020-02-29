package com.ajcm.kidstube.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.domain.Avatar
import com.ajcm.domain.User
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.common.ScopedViewModel
import com.ajcm.usecases.GetUserProfile
import com.ajcm.usecases.SaveUser
import kotlinx.coroutines.*
import kotlin.coroutines.resume

class SplashViewModel(
    private val localDB: LocalDB,
    private val saveUser: SaveUser,
    private val getUserProfile: GetUserProfile,
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
            launch {
                Log.i("SplashViewModel", "Getting user account")
                getUserIfExist()?.let {
                    localDB.userId = it.userId
                    localDB.userAvatar = it.userAvatar
                    Log.i("SplashViewModel", "User saved")
                }
                Log.i("SplashViewModel", "Playing Splash")
                playSplash()
            }
        }
    }

    private fun playSplash() = launch {
        _model.value = UiSplash.Loading
        delay(3150)
        _model.value = UiSplash.Navigate
    }

    @InternalCoroutinesApi
    private fun saveAccountName(accountName: String) {
        localDB.accountName = accountName
        launch {
            val user = getUserIfExist()
            if (user == null) {
                saveUser.invoke(User("", accountName, Avatar.MIA)).let {
                    localDB.userId = it
                }
            } else {
                localDB.userId = user.userId
                localDB.userAvatar = user.userAvatar
            }
            checkAccountName()
        }
    }

    private suspend fun getUserIfExist(): User? = suspendCancellableCoroutine { continuation ->
        val accountName = localDB.accountName
        if (accountName.isEmpty()) continuation.resume(null)
        Log.i("SplashViewModel", "Account: $accountName")
        launch {
            getUserProfile.invoke("userName", accountName).let { user ->
                Log.i("SplashViewModel", "GetUserProfile: $user")
                continuation.resume(user)
            }
        }
    }

    private fun loadError() {
        _model.value = UiSplash.LoadingError
    }

}