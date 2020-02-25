package com.ajcm.kidstube.ui.dashboard

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.extensions.*
import com.ajcm.kidstube.ui.adapters.VideoAdapter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import kotlinx.android.synthetic.main.dashboard_fragment.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class DashboardFragment : KidsFragment<UiDashboard, DashboardViewModel>(R.layout.dashboard_fragment),
    View.OnClickListener {

    override val viewModel: DashboardViewModel by currentScope.viewModel(this)

    private lateinit var adapter: VideoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
        addListeners()

        if (viewModel.model.value != null) {
            viewModel.dispatch(ActionDashboard.Refresh)
        }
    }

    private fun setUpViews() {
        adapter = VideoAdapter { video, view ->
            viewModel.dispatch(ActionDashboard.VideoSelected(video, view))
        }

        results.setUpLayoutManager()
        results.adapter = adapter

        imgProfile.loadRes(R.drawable.bck_avatar_kids_mia)
    }

    private fun addListeners() {
        imgProfile.setOnClickListener(this)
        imgSettings.setOnClickListener(this)
        contentSearch.setOnClickListener(this)
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
        }
    }

    override fun updateUi(state: UiState) {
        stopLoadingAnim()
        when (state) {
            is UiDashboard.Loading -> {
                hideActionViews()
                startLoadingAnim()
            }
            is UiDashboard.LoadingError -> {

            }
            UiDashboard.YoutubeStarted -> {
                viewModel.dispatch(ActionDashboard.Refresh)
            }
            is UiDashboard.RequestPermissions -> {
                when (state.exception) {
                    is UserRecoverableAuthIOException -> {
                        startActivityForResult(
                            state.exception.intent,
                            Constants.AUTH_CODE_REQUEST_CODE
                        )
                    }
                    is GooglePlayServicesAvailabilityIOException -> {
                        showGooglePlayServicesAvailabilityErrorDialog(state.exception.connectionStatusCode)
                    }
                    else -> {
                        println("DashboardFragment.updateUi --> Error: ${state.exception}")
                        viewModel.dispatch(ActionDashboard.LoadError)
                    }
                }
            }
            is UiDashboard.Content -> {
                showActionViews()
                adapter.videos = state.videos
            }
            is UiDashboard.NavigateTo -> {
                if (state.video != null && state.view != null) {
                    val extras = FragmentNavigatorExtras(Pair(state.view, state.video.videoId))
                    navigateTo(state.root.id, Bundle().apply {
                        putString(Constants.KEY_VIDEO_ID, state.video.videoId)
                    }, extras = extras)
                } else {
                    navigateTo(state.root.id)
                }
            }
        }
    }

    private fun hideActionViews() {
        imgProfile.hide()
        imgSettings.hide()
        contentSearch.hide()
    }

    private fun showActionViews() {
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

    private fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {
        GooglePlayServicesUtil.showErrorDialogFragment(
            connectionStatusCode,
            requireActivity(),
            this,
            Constants.REQUEST_GOOGLE_PLAY_SERVICES,
            null
        )
    }

    private fun haveGooglePlayServices() {
        if (credential.credential.selectedAccountName == null) {
            startActivityForResult(
                credential.credential.newChooseAccountIntent(),
                Constants.REQUEST_ACCOUNT_PICKER
            )
        } else {
            viewModel.dispatch(ActionDashboard.LoadError)
        }
    }

    private fun checkGooglePlayServicesAvailable() {
        val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {
            showGooglePlayServicesAvailabilityErrorDialog(resultCode)
        } else {
            viewModel.dispatch(ActionDashboard.Refresh)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_ACCOUNT_PICKER -> {
                val accountName = data?.extras?.getString(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    viewModel.dispatch(ActionDashboard.SaveAccount(accountName))
                } else {
                    viewModel.dispatch(ActionDashboard.LoadError)
                }
            }
            Constants.AUTH_CODE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.dispatch(ActionDashboard.Refresh)
                } else {
                    viewModel.dispatch(ActionDashboard.LoadError)
                }
            }
            Constants.REQUEST_GOOGLE_PLAY_SERVICES -> {
                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices()
                } else {
                    checkGooglePlayServicesAvailable()
                }
            }
        }
    }
}