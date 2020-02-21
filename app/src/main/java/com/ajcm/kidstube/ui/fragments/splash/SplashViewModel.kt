package com.ajcm.kidstube.ui.fragments.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ajcm.kidstube.common.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashViewModel(uiDispatcher: CoroutineDispatcher) : ScopedViewModel(uiDispatcher) {

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel>
        get() {
            if (_model.value == null) playSplash()
            return _model
        }

    init {
        initScope()
    }

    private fun playSplash() = launch {
        _model.value = UiModel.Loading
        delay(1200)
        _model.value = UiModel.Navigate
    }

    sealed class UiModel {
        object Loading : UiModel()
        object Navigate : UiModel()
    }

}