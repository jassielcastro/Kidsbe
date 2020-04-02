package com.ajcm.kidstube.ui.dashboard

import android.os.Bundle
import android.view.View
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.common.DashNav
import com.ajcm.kidstube.extensions.getDrawable
import com.ajcm.kidstube.ui.adapters.VideoAdapter
import com.ajcm.design.extensions.*
import kotlinx.android.synthetic.main.dashboard_fragment.*
import kotlinx.android.synthetic.main.generic_error.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DashboardFragment : KidsFragment<UiDashboard, DashboardViewModel>(R.layout.dashboard_fragment),
    View.OnClickListener {

    override val viewModel: DashboardViewModel by currentScope.viewModel(this) {
        parametersOf(arguments?.getString(Constants.KEY_USER_ID, "unknown"))
    }

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
        println("DashboardFragment.updateUi --> $state")
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
                adapter.videos = state.videos
            }
            is UiDashboard.NavigateTo -> {
                when (state.root) {
                    DashNav.PROFILE, DashNav.SETTINGS, DashNav.SEARCH -> {
                        navigateTo(state.root.id)
                    }
                    DashNav.VIDEO -> {
                        val userBundle = Bundle().apply {
                            putString(Constants.KEY_USER_ID, state.userId)
                            putSerializable(Constants.KEY_VIDEO_ID, state.video!!)
                        }
                        navigateTo(state.root.id, bundle = userBundle)
                    }
                    else -> {}
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