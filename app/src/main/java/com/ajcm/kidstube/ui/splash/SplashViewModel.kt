package com.ajcm.kidstube.ui.splash

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.domain.User
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.kidstube.ui.main.SongTrackListener
import com.ajcm.usecases.GetUserProfile
import com.ajcm.usecases.SaveUser
import kotlinx.coroutines.*
import kotlin.coroutines.resume

class SplashViewModel(
    private val localDB: LocalDataSource<User>,
    private val saveUser: SaveUser,
    private val getUserProfile: GetUserProfile,
    uiDispatcher: CoroutineDispatcher
) : ScopedViewModel<UiSplash>(uiDispatcher) {

    override val model: LiveData<UiSplash>
        get() {
            if (mModel.value == null) dispatch(ActionSplash.Start)
            return mModel
        }

    init {
        initScope()
    }

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
        if (!localDB.exist()) {
            consume(UiSplash.RequestAccount)
        } else {
            val user = getUserIfExist(localDB.getObject().userName)
            if (user != null) {
                localDB.save(user)
                playSplash()
            } else {
                consume(UiSplash.RequestAccount)
            }
        }
    }

    private fun playSplash() = launch {
        delay(3150)
        consume(UiSplash.Navigate(localDB.getObject().userId))
    }

    private fun saveAccountName(accountName: String) {
        launch {
            val user = getUserIfExist(accountName)
            if (user == null) {
                saveUser.invoke(User("", accountName)).let {
                    localDB.save(User(it, accountName))
                }
            } else {
                localDB.save(user)
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

    fun onResume(songTrackListener: SongTrackListener?) = launch {
        songTrackListener?.enableSong(true)
        songTrackListener?.onResumeSong()
    }

    fun onPlaySong(songTrackListener: SongTrackListener?) = launch {
        songTrackListener?.onPlaySong(R.raw.splash_komiku)
    }

}