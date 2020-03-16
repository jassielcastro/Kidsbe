package com.ajcm.kidstube.ui.playvideo

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.domain.User
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.extensions.getNextVideo
import com.ajcm.kidstube.extensions.getPositionOf
import com.ajcm.kidstube.model.VideoList
import com.ajcm.kidstube.ui.main.SongTrackListener
import com.ajcm.usecases.GetYoutubeVideos
import com.ajcm.usecases.SaveVideoWatched
import com.ajcm.usecases.UpdateUser
import com.payclip.design.extensions.delay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class PlayVideoViewModel(
    private val getYoutubeVideos: GetYoutubeVideos,
    private val saveVideoWatched: SaveVideoWatched,
    private val localDB: LocalDataSource<User>,
    private val updateUser: UpdateUser,
    uiDispatcher: CoroutineDispatcher
) :
    ScopedViewModel<UiPlayVideo>(uiDispatcher) {

    override val model: LiveData<UiPlayVideo>
        get() {
            if (mModel.value == null) consume(UiPlayVideo.Loading)
            return mModel
        }

    private var videoList: List<Video> = arrayListOf()
    private var sharedVideo: Video? = null

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            is ActionPlayVideo.ObtainVideo -> {
                actionState.bundle?.getSerializable(Constants.KEY_VIDEO_ID)?.let {
                    sharedVideo = it as Video
                }
                actionState.bundle?.getSerializable(Constants.KEY_VIDEO_LIST)?.let {
                    (it as? VideoList)?.let {  vl ->
                        videoList = vl.list
                    }
                }
            }
            is ActionPlayVideo.Start -> {
                if (sharedVideo != null) {
                    consume(UiPlayVideo.PlayVideo(sharedVideo!!))
                }
            }
            is ActionPlayVideo.PlayerViewReady -> {
                consume(UiPlayVideo.RenderYoutubePlayer(actionState.youTubePlayer))
            }
            ActionPlayVideo.Refresh -> {
                if (videoList.isEmpty()) {
                    refresh()
                } else {
                    showListOfVideos()
                }
            }
            ActionPlayVideo.LoadError -> {

            }
            is ActionPlayVideo.VideoSelected -> {
                playNextVideo(actionState.video)
            }
            ActionPlayVideo.PlayNextVideo -> {
                if (videoList.isNotEmpty()) {
                    val video = videoList.getNextVideo(sharedVideo?.videoId ?: "")
                    playNextVideo(video)
                }
            }
            ActionPlayVideo.ComputeScroll -> {
                val videoPosition = videoList.getPositionOf(sharedVideo?.videoId ?: "") + 1
                if (videoPosition >= videoList.size) {
                    consume(UiPlayVideo.ScrollToVideo(0))
                } else {
                    consume(UiPlayVideo.ScrollToVideo(videoPosition))
                }
            }
        }
    }

    private fun playNextVideo(video: Video) {
        launch {
            val newUser = localDB.getObject().copy(lastVideoWatched = video.videoId)
            localDB.save(newUser)
            updateUser.invoke(newUser)
            saveVideoWatched.invoke(video)
        }
        sharedVideo = video
        consume(UiPlayVideo.PlayVideo(video))
    }

    private fun showListOfVideos() {
        consume(UiPlayVideo.Content(videoList))
    }

    private fun refresh() = launch {
        consume(UiPlayVideo.Loading)
        val result = getYoutubeVideos.invoke(localDB.getObject().lastVideoWatched)
        if (result.videos.isNotEmpty()) {
            videoList = result.videos
            showListOfVideos()
        } else {
            consume(UiPlayVideo.RequestPermissions(result.exception))
        }
    }

    fun stopAppMusic(songTrackListener: SongTrackListener?) = launch {
        songTrackListener?.enableSong(false)
        songTrackListener?.onPauseSong()
    }

}