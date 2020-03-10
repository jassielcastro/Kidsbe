package com.ajcm.kidstube.ui.playvideo

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.domain.User
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.model.VideoList
import com.ajcm.kidstube.ui.main.SongTrackListener
import com.ajcm.usecases.GetYoutubeVideos
import com.ajcm.usecases.UpdateUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class PlayVideoViewModel(
    private val getYoutubeVideos: GetYoutubeVideos,
    private val localDB: LocalDataSource<User>,
    private val updateUser: UpdateUser,
    uiDispatcher: CoroutineDispatcher
) :
    ScopedViewModel<UiPlayVideo>(uiDispatcher) {

    override val model: LiveData<UiPlayVideo>
        get() {
            if (mModel.value == null) dispatch(ActionPlayVideo.StartYoutube)
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
                consume(UiPlayVideo.Loading)
                actionState.bundle?.getSerializable(Constants.KEY_VIDEO_ID)?.let {
                    sharedVideo = it as Video
                }
                actionState.bundle?.getSerializable(Constants.KEY_VIDEO_LIST)?.let {
                    (it as? VideoList)?.let {  vl ->
                        videoList = vl.list
                    }
                }
            }
            ActionPlayVideo.StartYoutube -> {
                launch {
                    getYoutubeVideos.startYoutubeWith(localDB.getObject().userName)
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
                    consume(UiPlayVideo.Content(videoList.filter { it.videoId != sharedVideo?.videoId }.shuffled()))
                }
            }
            is ActionPlayVideo.SaveLastVideo -> {
                launch {
                    val newUser = localDB.getObject().copy(lastVideoWatched = actionState.lastVideo.videoId)
                    localDB.save(newUser)
                    updateUser.invoke(newUser)
                }
            }
            ActionPlayVideo.LoadError -> {

            }
            is ActionPlayVideo.VideoSelected -> {
                sharedVideo = actionState.video
                consume(UiPlayVideo.PlayVideo(actionState.video))
            }
            ActionPlayVideo.PlayNextVideo -> {
                if (videoList.isNotEmpty()) {
                    val video = videoList[0]
                    consume(UiPlayVideo.PlayVideo(video))
                }
            }
        }
    }

    private fun refresh() = launch {
        consume(UiPlayVideo.Loading)
        val result = getYoutubeVideos.invoke(localDB.getObject().lastVideoWatched)
        if (result.videos.isNotEmpty()) {
            videoList = result.videos
            consume(UiPlayVideo.Content(result.videos))
        } else {
            consume(UiPlayVideo.RequestPermissions(result.exception))
        }
    }

    fun stopAppMusic(songTrackListener: SongTrackListener?) = launch {
        songTrackListener?.enableSong(false)
        songTrackListener?.onPauseSong()
    }

}