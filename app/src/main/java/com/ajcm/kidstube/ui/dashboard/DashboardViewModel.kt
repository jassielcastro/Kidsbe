package com.ajcm.kidstube.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
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
            if (mModel.value == null) dispatch(ActionDashboard.Start)
            return mModel
        }

    var videos: List<Video> = emptyList()

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionDashboard.Start -> {
                consume(UiDashboard.UpdateUserInfo(localDB.userAvatar))
            }
            ActionDashboard.StartYoutube -> {
                getYoutubeVideos.startYoutubeWith(localDB.accountName)
                consume(UiDashboard.YoutubeStarted)
            }
            ActionDashboard.Refresh -> {
                if (videos.isEmpty()) {
                    refresh()
                } else {
                    consume(UiDashboard.Content(videos.shuffled()))
                }
            }
            is ActionDashboard.SaveAccount -> {
                localDB.accountName = actionState.accountName
                refresh()
            }
            is ActionDashboard.ParseException -> {
                val msg = parseError(actionState.exception)
                consume(UiDashboard.LoadingError(msg))
            }
            is ActionDashboard.LoadError -> {
                consume(UiDashboard.LoadingError(actionState.msg))
            }
            is ActionDashboard.VideoSelected -> {
                consume(UiDashboard.NavigateTo(DashNav.VIDEO, actionState.video))
            }
            is ActionDashboard.ChangeRoot -> {
                consume(UiDashboard.NavigateTo(actionState.root, null))
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
        consume(UiDashboard.Loading)
        val result = getYoutubeVideos.invoke(localDB.lastVideoId)
        if (result.videos.isNotEmpty()) {
            videos = result.videos
            consume(UiDashboard.Content(result.videos))
        } else {
            consume(UiDashboard.RequestPermissions(result.exception))
        }
    }

}