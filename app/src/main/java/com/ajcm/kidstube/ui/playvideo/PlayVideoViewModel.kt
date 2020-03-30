package com.ajcm.kidstube.ui.playvideo

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.domain.User
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.extensions.delete
import com.ajcm.kidstube.extensions.getNextVideo
import com.ajcm.kidstube.extensions.getPositionOf
import com.ajcm.kidstube.ui.main.SongTrackListener
import com.ajcm.usecases.GetYoutubeVideos
import com.ajcm.usecases.UpdateUser
import com.ajcm.usecases.VideoWatched
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class PlayVideoViewModel(
    private val getYoutubeVideos: GetYoutubeVideos,
    private val videoWatched: VideoWatched,
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
    private var isFirstLoad = true

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        println("PlayVideoViewModel.dispatch --> $actionState")
        when (actionState) {
            is ActionPlayVideo.ObtainVideo -> {
                actionState.bundle?.getSerializable(Constants.KEY_VIDEO_ID)?.let {
                    sharedVideo = it as Video
                }
            }
            is ActionPlayVideo.Start -> {
                sharedVideo?.let {
                    playNextVideo(it)
                }
            }
            is ActionPlayVideo.PlayerViewReady -> {
                launch {
                    getYoutubeVideos.startYoutubeWith(localDB.getObject().userName)
                    consume(UiPlayVideo.RenderYoutubePlayer(actionState.youTubePlayer))
                }
            }
            ActionPlayVideo.Refresh -> {
                if (videoList.isEmpty()) {
                    launch {
                        refresh(sharedVideo?.videoId ?: localDB.getObject().lastVideoWatched)
                    }
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
                if (!isFirstLoad) {
                    val videoPosition = videoList.getPositionOf(sharedVideo?.videoId ?: "") + 1
                    if (videoPosition >= videoList.size) {
                        consume(UiPlayVideo.ScrollToVideo(0))
                    } else {
                        consume(UiPlayVideo.ScrollToVideo(videoPosition))
                    }
                }
                isFirstLoad = false
            }
            ActionPlayVideo.BlockCurrentVideo -> {
                sharedVideo?.let {
                    launch {
                        videoWatched.addToBlackList(it.title)
                    }
                    val video = videoList.getNextVideo(it.videoId)
                    videoList = videoList.delete(it)
                    playNextVideo(video)
                }
            }
            ActionPlayVideo.LoadVideosByCurrent -> {
                sharedVideo?.let {
                    refresh(it.videoId)
                }
            }
        }
    }

    private fun playNextVideo(video: Video) {
        launch {
            val newUser = localDB.getObject().copy(lastVideoWatched = video.videoId)
            localDB.save(newUser)
            updateUser.invoke(newUser)
            videoWatched.save(video)
        }
        sharedVideo = video
        consume(UiPlayVideo.PlayVideo(video))
    }

    private fun showListOfVideos() {
        consume(UiPlayVideo.Content(videoList))
    }

    private fun refresh(videoId: String) = launch {
        consume(UiPlayVideo.Loading)
        val result = getYoutubeVideos.invoke(videoId, attempts = 2)
        if (result.videos.isNotEmpty()) {
            val videoIndex = videoList.getPositionOf(videoId)

            videoList = (if (videoIndex != -1 && (videoIndex - 1) < videoList.size) {
                val tempVideos = videoList.toMutableList()
                tempVideos.addAll(videoIndex, result.videos)
                tempVideos.toList()
            } else {
                (videoList + result.videos)
            }).shuffled()

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