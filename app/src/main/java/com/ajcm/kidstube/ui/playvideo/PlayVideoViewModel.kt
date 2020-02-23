package com.ajcm.kidstube.ui.playvideo

import androidx.lifecycle.LiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.common.ScopedViewModel
import com.ajcm.usecases.GetYoutubeVideos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class PlayVideoViewModel(
    private val getYoutubeVideos: GetYoutubeVideos,
    private val localDB: LocalDB,
    uiDispatcher: CoroutineDispatcher
) :
    ScopedViewModel<UiPlayVideo>(uiDispatcher) {

    override val model: LiveData<UiPlayVideo>
        get() {
            if (_model.value == null) dispatch(ActionPlayVideo.StartYoutube)
            return _model
        }

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionPlayVideo.StartYoutube -> {
                getYoutubeVideos.startYoutubeWith(localDB.accountName)
            }
            is ActionPlayVideo.Start -> {
                actionState.bundle?.getString(Constants.KEY_VIDEO_ID)?.let {
                    _model.value = UiPlayVideo.PlayVideo(it)
                }
            }
            is ActionPlayVideo.PlayerViewReady -> {
                _model.value = UiPlayVideo.RenderYoutubePlayer(actionState.youTubePlayer)
            }
            ActionPlayVideo.Refresh -> {
                refresh()
            }
            is ActionPlayVideo.SaveLastVideoId -> {
                localDB.lastVideoId = actionState.lastVideoId
            }
            ActionPlayVideo.LoadError -> {

            }
            is ActionPlayVideo.VideoSelected -> {
                _model.value = UiPlayVideo.PlayVideo(actionState.video.videoId)
            }
        }
    }

    private fun refresh() = launch {
        _model.value = UiPlayVideo.Loading
        val result = getYoutubeVideos.invoke(localDB.lastVideoId)
        if (result.videos.isNotEmpty()) {
            _model.value = UiPlayVideo.Content(result.videos)
        } else {
            _model.value = UiPlayVideo.RequestPermissions(result.exception)
        }
    }

}