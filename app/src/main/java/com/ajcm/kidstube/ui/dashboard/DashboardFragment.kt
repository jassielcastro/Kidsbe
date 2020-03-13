package com.ajcm.kidstube.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.extensions.getDrawable
import com.ajcm.kidstube.model.VideoList
import com.ajcm.kidstube.ui.adapters.VideoAdapter
import com.payclip.design.extensions.*
import kotlinx.android.synthetic.main.dashboard_fragment.*
import kotlinx.android.synthetic.main.generic_error.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class DashboardFragment : KidsFragment<UiDashboard, DashboardViewModel>(R.layout.dashboard_fragment),
    View.OnClickListener {

    override val viewModel: DashboardViewModel by currentScope.viewModel(this)

    private lateinit var adapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onPlaySong(songTrackListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
        addListeners()

        if (viewModel.videos.isNotEmpty()) {
            viewModel.dispatch(ActionDashboard.Start)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(songTrackListener)
    }

    private fun setUpViews() {
        adapter = VideoAdapter { video ->
            viewModel.dispatch(ActionDashboard.VideoSelected(video))
        }

        results.setUpLayoutManager()
        results.adapter = adapter

        imgProfile.loadRes(R.drawable.bck_avatar_kids_mia)
    }

    private fun addListeners() {
        imgProfile.setOnClickListener(this)
        imgSettings.setOnClickListener(this)
        contentSearch.setOnClickListener(this)
        btnErrorAction.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgProfile -> {
                viewModel.dispatch(ActionDashboard.ChangeRoot(DashNav.PROFILE))
            }
            R.id.imgSettings -> {
                viewModel.dispatch(ActionDashboard.ChangeRoot(DashNav.SETTINGS))
            }
            R.id.contentSearch -> {
                viewModel.dispatch(ActionDashboard.ChangeRoot(DashNav.SEARCH))
            }
            R.id.btnErrorAction -> {
                viewModel.dispatch(ActionDashboard.StartYoutube)
            }
        }
    }

    override fun updateUi(state: UiState) {
        contentError.hide()
        when (state) {
            is UiDashboard.Loading -> {
                hideActionViews()
                startLoadingAnim()
            }
            is UiDashboard.LoadingError -> {
                stopLoadingAnim()
                txtErrorTitle.text = state.msg
                contentError.show()
            }
            is UiDashboard.UpdateUserInfo -> {
                imgProfile.loadRes(state.avatar.getDrawable())
                viewModel.dispatch(ActionDashboard.StartYoutube)
            }
            UiDashboard.YoutubeStarted -> {
                viewModel.dispatch(ActionDashboard.Refresh)
            }
            is UiDashboard.RequestPermissions -> {
                stopLoadingAnim()
                viewModel.dispatch(ActionDashboard.ParseException(state.exception))
            }
            is UiDashboard.Content -> {
                stopLoadingAnim()
                showActionViews()
                adapter.videos = state.videos.shuffled()
            }
            is UiDashboard.NavigateTo -> {
                when (state.root) {
                    DashNav.PROFILE -> {
                        navigateTo(state.root.id)
                    }
                    DashNav.SETTINGS -> {
                        val extras = FragmentNavigatorExtras(
                            imgProfile to ViewCompat.getTransitionName(imgProfile)!!)
                        navigateTo(state.root.id, extras = extras)
                    }
                    DashNav.SEARCH -> {
                        navigateTo(state.root.id)
                    }
                    DashNav.VIDEO -> {
                        navigateTo(state.root.id, Bundle().apply {
                            putSerializable(Constants.KEY_USER_ID, state.userId)
                            putSerializable(Constants.KEY_VIDEO_ID, state.video!!)
                            putSerializable(Constants.KEY_VIDEO_LIST, VideoList(viewModel.videos))
                        })
                    }
                }
            }
        }
    }

    private fun hideActionViews() {
        results.hide()
        imgProfile.hide()
        imgSettings.hide()
        contentSearch.hide()
    }

    private fun showActionViews() {
        results.show()
        imgProfile.show()
        imgSettings.show()
        contentSearch.show()
    }

    private fun startLoadingAnim() {
        progress.show()
        progress.playAnimation()
    }

    private fun stopLoadingAnim() {
        progress.hide()
        progress.cancelAnimation()
    }

}