package com.ajcm.kidstube.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.domain.Video
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.kidstube.ui.main.SongTrackListener
import com.ajcm.usecases.GetYoutubeVideos
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getYoutubeVideos: GetYoutubeVideos,
    private val localDB: LocalDataSource,
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
                launch {
                    consume(UiDashboard.UpdateUserInfo(localDB.getUser().userAvatar))
                }
            }
            ActionDashboard.StartYoutube -> {
                launch {
                    getYoutubeVideos.startYoutubeWith(localDB.getUser().userName)
                    consume(UiDashboard.YoutubeStarted)
                }
            }
            ActionDashboard.Refresh -> {
                if (videos.isEmpty()) {
                    refresh()
                } else {
                    consume(UiDashboard.Content(videos.shuffled()))
                }
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
        val result = getYoutubeVideos.invoke(localDB.getUser().lastVideoWatched)
        if (result.videos.isNotEmpty()) {
            videos = result.videos
            consume(UiDashboard.Content(result.videos))
        } else {
            consume(UiDashboard.RequestPermissions(result.exception))
        }
    }

    fun onResume(songTrackListener: SongTrackListener?) = launch {
        songTrackListener?.enableSong(true)
        songTrackListener?.onResumeSong()
    }

    fun onPlaySong(songTrackListener: SongTrackListener?) = launch {
        songTrackListener?.onPlaySong(R.raw.dashboard_ben_smile)
    }

}