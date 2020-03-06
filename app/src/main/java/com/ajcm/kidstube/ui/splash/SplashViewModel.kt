package com.ajcm.kidstube.ui.splash

import androidx.lifecycle.LiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.domain.Avatar
import com.ajcm.domain.User
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
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
        if (localDB.accountName.isEmpty()) {
            consume(UiSplash.RequestAccount)
        } else {
            launch {
                getUserIfExist()?.let {
                    localDB.userId = it.userId
                    localDB.userAvatar = it.userAvatar
                }
                playSplash()
            }
        }
    }

    private fun playSplash() = launch {
        delay(3150)
        consume(UiSplash.Navigate)
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
        launch {
            getUserProfile.invoke("userName", accountName).let { user ->
                continuation.resume(user)
            }
        }
    }

    private fun loadError() {
        consume(UiSplash.LoadingError)
    }

}