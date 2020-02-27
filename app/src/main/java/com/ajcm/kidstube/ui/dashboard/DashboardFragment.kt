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
import com.ajcm.kidstube.model.VideoList
import com.ajcm.kidstube.ui.adapters.VideoAdapter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import kotlinx.android.synthetic.main.dashboard_fragment.*
import kotlinx.android.synthetic.main.generic_error.*
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

        //when is returned by another fragment instance
        if (viewModel.videos.isNotEmpty()) {
            updateUi(UiDashboard.Content(viewModel.videos))
        }
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
        stopLoadingAnim()
        contentError.hide()
        when (state) {
            is UiDashboard.Loading -> {
                hideActionViews()
                startLoadingAnim()
            }
            is UiDashboard.LoadingError -> {
                txtErrorTitle.text = state.msg
                contentError.show()
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
                        viewModel.dispatch(ActionDashboard.ParseException(state.exception))
                    }
                }
            }
            is UiDashboard.Content -> {
                showActionViews()
                adapter.videos = state.videos.shuffled()
            }
            is UiDashboard.NavigateTo -> {
                if (state.video != null) {
                    navigateTo(state.root.id, Bundle().apply {
                        putString(Constants.KEY_VIDEO_ID, state.video.videoId)
                        putSerializable(Constants.KEY_VIDEO_LIST, VideoList(viewModel.videos))
                    })
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

    private fun haveSelectedAccountName() {
        if (credential.credential.selectedAccountName == null) {
            startActivityForResult(
                credential.credential.newChooseAccountIntent(),
                Constants.REQUEST_ACCOUNT_PICKER
            )
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
                    viewModel.dispatch(ActionDashboard.LoadError("No has seleccionado una cuenta para poder utilizar Kidstube!"))
                }
            }
            Constants.AUTH_CODE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.dispatch(ActionDashboard.Refresh)
                } else {
                    viewModel.dispatch(ActionDashboard.LoadError("Vuelve a iniciar sesión para continuar con Kidstube!"))
                }
            }
            Constants.REQUEST_GOOGLE_PLAY_SERVICES -> {
                if (resultCode == Activity.RESULT_OK) {
                    haveSelectedAccountName()
                } else {
                    checkGooglePlayServicesAvailable()
                }
            }
        }
    }
}