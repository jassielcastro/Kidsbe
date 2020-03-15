package com.ajcm.kidstube.ui.search

import androidx.lifecycle.LiveData
import com.ajcm.data.source.LocalDataSource
import com.ajcm.domain.User
import com.ajcm.domain.Video
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.arch.ScopedViewModel
import com.ajcm.kidstube.common.DashNav
import com.ajcm.kidstube.common.VideoAction
import com.ajcm.usecases.SearchYoutubeVideos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class SearchViewModel(private val searchVideos: SearchYoutubeVideos, private val localDb: LocalDataSource<User>, uiDispatcher: CoroutineDispatcher): ScopedViewModel<UiSearch>(uiDispatcher) {

    override val model: LiveData<UiSearch>
        get() {
            if (mModel.value == null) consume(UiSearch.Start)
            return mModel
        }

    var videos: List<Video> = emptyList()

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionSearch.PrepareYoutube -> {
                launch {
                    searchVideos.startYoutubeWith(localDb.getObject().userName)
                }
            }
            is ActionSearch.Search -> {
                actionState.byText?.let {
                    search(it)
                }
            }
            is ActionSearch.VideoSelected -> {
                when (val act = actionState.videoAction) {
                    is VideoAction.Play -> {
                        launch {
                            consume(UiSearch.NavigateTo(DashNav.SEARCH_TO_VIDEO, act.video, localDb.getObject().userId))
                        }
                    }
                }
            }
        }
    }

    private fun search(byText: String) = launch {
        if (byText.isNotEmpty()) {
            consume(UiSearch.Loading)
            val result = searchVideos.invoke(byText)
            if (result.videos.isNotEmpty()) {
                videos = result.videos
                consume(UiSearch.Content(videos))
            } else {
                consume(UiSearch.LoadError)
            }
        } else {
            consume(UiSearch.Content(emptyList()))
        }
    }

}