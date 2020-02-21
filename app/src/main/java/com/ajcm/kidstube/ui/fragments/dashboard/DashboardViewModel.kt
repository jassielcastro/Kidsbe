package com.ajcm.kidstube.ui.fragments.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ajcm.domain.Video
import com.ajcm.kidstube.common.ScopedViewModel
import com.ajcm.usecases.GetPopularVideos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getPopularVideos: GetPopularVideos,
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
        data class Content(val videos: List<Video>) : UiModel()
        data class Navigate(val videos: Video) : UiModel()
    }

    init {
        initScope()
    }

    private fun refresh() = launch {
        _model.value = UiModel.Loading
        //_model.value = UiModel.Content(getPopularVideos.invoke("KH_VRLMGHO4"))
    }

    fun onMovieClicked(video: Video) {
        _model.value = UiModel.Navigate(video)
    }

}