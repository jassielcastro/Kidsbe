package com.ajcm.kidstube.ui.playvideo

import androidx.lifecycle.LiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.common.ScopedViewModel
import com.ajcm.kidstube.model.VideoList
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

    private var videoList: List<Video> = arrayListOf()
    private var sharedVideoId: String? = null
    private var videoThumbnail: String? = null

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            is ActionPlayVideo.ObtainVideo -> {
                _model.value = UiPlayVideo.Loading
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
                getYoutubeVideos.startYoutubeWith(localDB.accountName)
            }
            is ActionPlayVideo.Start -> {
                if (sharedVideoId != null && videoThumbnail != null) {
                    _model.value = UiPlayVideo.PlayVideo(sharedVideoId!!, videoThumbnail!!)
                }
            }
            is ActionPlayVideo.PlayerViewReady -> {
                _model.value = UiPlayVideo.RenderYoutubePlayer(actionState.youTubePlayer)
            }
            ActionPlayVideo.Refresh -> {
                if (videoList.isEmpty()) {
                    refresh()
                } else {
                    _model.value = UiPlayVideo.Content(videoList.shuffled())
                }
            }
            is ActionPlayVideo.SaveLastVideoId -> {
                localDB.lastVideoId = actionState.lastVideoId
            }
            ActionPlayVideo.LoadError -> {

            }
            is ActionPlayVideo.VideoSelected -> {
                videoThumbnail = actionState.video.thumbnail
                _model.value = UiPlayVideo.PlayVideo(actionState.video.videoId, actionState.video.thumbnail)
            }
            ActionPlayVideo.PlayNextVideo -> {
                if (videoList.isNotEmpty()) {
                    val video = videoList[0]
                    videoThumbnail = video.thumbnail
                    _model.value = UiPlayVideo.PlayVideo(video.videoId, video.thumbnail)
                }
            }
        }
    }

    private fun refresh() = launch {
        _model.value = UiPlayVideo.Loading
        val result = getYoutubeVideos.invoke(localDB.lastVideoId)
        if (result.videos.isNotEmpty()) {
            videoList = result.videos
            _model.value = UiPlayVideo.Content(result.videos)
        } else {
            _model.value = UiPlayVideo.RequestPermissions(result.exception)
        }
    }

}