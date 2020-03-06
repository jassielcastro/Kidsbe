package com.ajcm.kidstube.ui.playvideo

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.model.VideoList
import com.ajcm.kidstube.ui.main.SongTrackListener
import com.ajcm.usecases.GetYoutubeVideos
import com.ajcm.usecases.UpdateUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class PlayVideoViewModel(
    private val getYoutubeVideos: GetYoutubeVideos,
    private val localDB: LocalDataSource,
    private val updateUser: UpdateUser,
    uiDispatcher: CoroutineDispatcher
) :
    ScopedViewModel<UiPlayVideo>(uiDispatcher) {

    @InternalCoroutinesApi
    override val model: LiveData<UiPlayVideo>
        get() {
            if (mModel.value == null) dispatch(ActionPlayVideo.StartYoutube)
            return mModel
        }

    private var videoList: List<Video> = arrayListOf()
    private var sharedVideoId: String? = null
    private var videoThumbnail: String? = null

    init {
        initScope()
    }

    @InternalCoroutinesApi
    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            is ActionPlayVideo.ObtainVideo -> {
                consume(UiPlayVideo.Loading)
                actionState.bundle?.getString(Constants.KEY_VIDEO_ID)?.let {
                    sharedVideoId = it
                }
                actionState.bundle?.getString(Constants.KEY_VIDEO_THUMBNAIL)?.let {
                    videoThumbnail = it
                }
                actionState.bundle?.getSerializable(Constants.KEY_VIDEO_LIST)?.let {
                    (it as? VideoList)?.let {  vl ->
                        videoList = vl.list
                    }
                }
            }
            ActionPlayVideo.StartYoutube -> {
                launch {
                    getYoutubeVideos.startYoutubeWith(localDB.getUser().userName)
                }
            }
            is ActionPlayVideo.Start -> {
                if (sharedVideoId != null && videoThumbnail != null) {
                    consume(UiPlayVideo.PlayVideo(sharedVideoId!!, videoThumbnail!!))
                }
            }
            is ActionPlayVideo.PlayerViewReady -> {
                consume(UiPlayVideo.RenderYoutubePlayer(actionState.youTubePlayer))
            }
            ActionPlayVideo.Refresh -> {
                if (videoList.isEmpty()) {
                    refresh()
                } else {
                    consume(UiPlayVideo.Content(videoList.shuffled()))
                }
            }
            is ActionPlayVideo.SaveLastVideoId -> {
                launch {
                    val newUser = localDB.getUser().copy(lastVideoWatched = actionState.lastVideoId)
                    localDB.saveUser(newUser)
                    updateUser.invoke(newUser)
                }
            }
            ActionPlayVideo.LoadError -> {

            }
            is ActionPlayVideo.VideoSelected -> {
                videoThumbnail = actionState.video.thumbnail
                consume(UiPlayVideo.PlayVideo(actionState.video.videoId, actionState.video.thumbnail))
            }
            ActionPlayVideo.PlayNextVideo -> {
                if (videoList.isNotEmpty()) {
                    val video = videoList[0]
                    videoThumbnail = video.thumbnail
                    consume(UiPlayVideo.PlayVideo(video.videoId, video.thumbnail))
                }
            }
        }
    }

    private fun refresh() = launch {
        consume(UiPlayVideo.Loading)
        val result = getYoutubeVideos.invoke(localDB.getUser().lastVideoWatched)
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