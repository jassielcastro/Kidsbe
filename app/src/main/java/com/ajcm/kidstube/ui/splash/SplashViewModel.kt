package com.ajcm.kidstube.ui.splash

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.domain.User
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.usecases.GetUserProfile
import com.ajcm.usecases.SaveUser
import kotlinx.coroutines.*
import kotlin.coroutines.resume

class SplashViewModel(
    private val localDB: LocalDataSource,
    private val saveUser: SaveUser,
    private val getUserProfile: GetUserProfile,
    uiDispatcher: CoroutineDispatcher
) : ScopedViewModel<UiSplash>(uiDispatcher) {

    @InternalCoroutinesApi
    override val model: LiveData<UiSplash>
        get() {
            if (mModel.value == null) dispatch(ActionSplash.Start)
            return mModel
        }

    init {
        initScope()
    }

    @InternalCoroutinesApi
    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionSplash.Start -> {
                consume(UiSplash.Loading)
            }
            ActionSplash.RequestPermissions -> {
                consume(UiSplash.CheckPermissions)
            }
            ActionSplash.ValidateAccount -> {
                launch {
                    checkAccountName()
                }
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

    private suspend fun checkAccountName() {
        if (!localDB.existUser()) {
            consume(UiSplash.RequestAccount)
        } else {
            val user = getUserIfExist(localDB.getUser().userName)
            if (user != null) {
                localDB.saveUser(user)
                playSplash()
            } else {
                consume(UiSplash.RequestAccount)
            }
        }
    }

    private fun playSplash() = launch {
        delay(3150)
        consume(UiSplash.Navigate)
    }

    @InternalCoroutinesApi
    private fun saveAccountName(accountName: String) {
        launch {
            val user = getUserIfExist(accountName)
            if (user == null) {
                saveUser.invoke(User("", accountName)).let {
                    localDB.saveUser(User(it, accountName))
                }
            } else {
                localDB.saveUser(user)
            }
            checkAccountName()
        }
    }

    private suspend fun getUserIfExist(accountName: String): User? = suspendCancellableCoroutine { continuation ->
        launch {
            if (accountName.isEmpty()) continuation.resume(null)
            launch {
                getUserProfile.invoke("userName", accountName).let { user ->
                    continuation.resume(user)
                }
            }
        }
    }

    private fun loadError() {
        consume(UiSplash.LoadingError)
    }

}