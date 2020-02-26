package com.ajcm.kidstube.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.common.ScopedViewModel
import com.ajcm.usecases.GetYoutubeVideos
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getYoutubeVideos: GetYoutubeVideos,
    private val localDB: LocalDB,
    uiDispatcher: CoroutineDispatcher
) : ScopedViewModel<UiDashboard>(uiDispatcher) {

    override val model: LiveData<UiDashboard>
        get() {
            if (_model.value == null) dispatch(ActionDashboard.StartYoutube)
            return _model
        }

    var videos: List<Video> = emptyList()

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionDashboard.StartYoutube -> {
                getYoutubeVideos.startYoutubeWith(localDB.accountName)
                _model.value = UiDashboard.YoutubeStarted
            }
            ActionDashboard.Refresh -> {
                refresh()
            }
            is ActionDashboard.SaveAccount -> {
                localDB.accountName = actionState.accountName
                refresh()
            }
            is ActionDashboard.ParseException -> {
                val msg = parseError(actionState.exception)
                _model.value = UiDashboard.LoadingError(msg)
            }
            is ActionDashboard.LoadError -> {
                _model.value = UiDashboard.LoadingError(actionState.msg)
            }
            is ActionDashboard.VideoSelected -> {
                _model.value = UiDashboard.NavigateTo(DashNav.VIDEO, actionState.video, actionState.view)
            }
            is ActionDashboard.ChangeRoot -> {
                _model.value = UiDashboard.NavigateTo(actionState.root, null, null)
            }
        }
    }

    private fun parseError(exception: Exception?): String {
        Log.e("DashboardViewModel", exception?.toString() ?: "Unspecific error")
        return when(exception) {
            is GoogleJsonResponseException -> {
                exception.details.message
            }
            else -> "Ocurrió un error al procesar la solicitud. Intenta de nuevo!"
        }
    }

    private fun refresh() = launch {
        _model.value = UiDashboard.Loading
        val result = getYoutubeVideos.invoke(localDB.lastVideoId)
        if (result.videos.isNotEmpty()) {
            videos = result.videos
            _model.value = UiDashboard.Content(result.videos)
        } else {
            _model.value = UiDashboard.RequestPermissions(result.exception)
        }
    }

}