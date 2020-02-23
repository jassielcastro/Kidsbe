package com.ajcm.kidstube.ui.dashboard

import androidx.lifecycle.LiveData
import com.ajcm.data.database.LocalDB
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.common.ScopedViewModel
import com.ajcm.usecases.GetYoutubeVideos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getYoutubeVideos: GetYoutubeVideos,
    private val localDB: LocalDB,
    uiDispatcher: CoroutineDispatcher
) : ScopedViewModel<UiDashboard>(uiDispatcher) {

    override val model: LiveData<UiDashboard>
        get() {
            if (_model.value == null) dispatch(ActionDashboard.Refresh)
            return _model
        }

    init {
        initScope()
        dispatch(ActionDashboard.StartYoutube)
    }

    override fun dispatch(actionState: ActionState) {
        when (actionState) {
            ActionDashboard.StartYoutube -> {
                getYoutubeVideos.startYoutubeWith(localDB.accountName)
            }
            ActionDashboard.Refresh -> {
                refresh()
            }
            is ActionDashboard.SaveAccount -> {
                localDB.accountName = actionState.accountName
                refresh()
            }
            ActionDashboard.LoadError -> {
                _model.value = UiDashboard.LoadingError
            }
            is ActionDashboard.VideoSelected -> {
                _model.value = UiDashboard.NavigateTo(DashNav.VIDEO, actionState.video)
            }
            is ActionDashboard.ChangeRoot -> {
                _model.value = UiDashboard.NavigateTo(actionState.root, null)
            }
        }
    }

    private fun refresh() = launch {
        _model.value = UiDashboard.Loading
        val result = getYoutubeVideos.invoke(localDB.lastVideoId)
        if (result.videos.isNotEmpty()) {
            _model.value = UiDashboard.Content(result.videos)
        } else {
            _model.value = UiDashboard.RequestPermissions(result.exception)
        }
    }

}