package com.ajcm.kidstube.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.kidstube.common.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(private val localDB: LocalDB, uiDispatcher: CoroutineDispatcher) : ScopedViewModel(uiDispatcher) {

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel>
        get() {
            if (_model.value == null) checkAccountName()
            return _model
        }

    init {
        initScope()
    }

    private fun checkAccountName() {
        _model.value = UiModel.Loading
        if (localDB.accountName.isEmpty()) {
            _model.value = UiModel.RequestAccount
        } else {
            playSplash()
        }
    }

    private fun playSplash() = launch {
        _model.value = UiModel.Loading
        delay(5150)
        _model.value = UiModel.Navigate
    }

    fun saveAccountName(accountName: String) {
        localDB.accountName = accountName
        checkAccountName()
    }

    fun loadError() {
        _model.value = UiModel.LoadingError
    }

    sealed class UiModel {
        object Loading : UiModel()
        object LoadingError : UiModel()
        object RequestAccount : UiModel()
        object Navigate : UiModel()
    }

}