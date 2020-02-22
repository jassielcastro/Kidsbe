package com.ajcm.kidstube.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.domain.Video
import com.ajcm.kidstube.common.ScopedViewModel
import com.ajcm.usecases.GetYoutubeVideos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getYoutubeVideos: GetYoutubeVideos,
    private val localDB: LocalDB,
    uiDispatcher: CoroutineDispatcher
): ScopedViewModel(uiDispatcher) {

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel>
        get() {
            if (_model.value == null) refresh()
            return _model
        }

    sealed class UiModel {
        object Loading : UiModel()
        object LoadingError : UiModel()
        data class RequestPermissions(val exception: Exception?) : UiModel()
        data class Content(val videos: List<Video>) : UiModel()
        data class Navigate(val videos: Video) : UiModel()
    }

    init {
        initScope()
        getYoutubeVideos.startYoutubeWith(localDB.accountName)
    }

    fun refresh() = launch {
        _model.value = UiModel.Loading
        val result = getYoutubeVideos.invoke("KH_VRLMGHO4")
        if (result.videos.isNotEmpty()) {
            _model.value = UiModel.Content(result.videos)
        } else {
            _model.value = UiModel.RequestPermissions(result.exception)
        }
    }

    fun saveAccountName(accountName: String) {
        localDB.accountName = accountName
        refresh()
    }

    fun loadError() {
        _model.value = UiModel.LoadingError
    }

    fun onVideoClicked(video: Video) {
        _model.value = UiModel.Navigate(video)
    }

}