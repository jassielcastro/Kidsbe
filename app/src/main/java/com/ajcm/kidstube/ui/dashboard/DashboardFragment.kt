package com.ajcm.kidstube.ui.dashboard

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ajcm.data.auth.Constants
import com.ajcm.kidstube.R
import com.ajcm.kidstube.common.GoogleCredential
import com.ajcm.kidstube.extensions.*
import com.ajcm.kidstube.ui.adapters.VideoAdapter
import com.ajcm.kidstube.ui.dashboard.DashboardViewModel.UiModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import kotlinx.android.synthetic.main.dashboard_fragment.*
import org.koin.android.ext.android.inject
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment(R.layout.dashboard_fragment) {

    private val viewModel: DashboardViewModel by currentScope.viewModel(this)

    private val credential: GoogleCredential by inject()

    private lateinit var adapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.model.observe(this, Observer(::updateUi))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = VideoAdapter(viewModel::onVideoClicked)
        results.setUpLayoutManager()
        results.adapter = adapter

        imgProfile.loadRes(R.drawable.bck_avatar_kids_mia)
    }

    private fun updateUi(model: UiModel) {
        stopLoadingAnim()
        when (model) {
            is UiModel.Loading -> {
                hideActionViews()
                startLoadingAnim()
            }
            is UiModel.LoadingError -> {

            }
            is UiModel.RequestPermissions -> {
                when(model.exception) {
                    is UserRecoverableAuthIOException -> {
                        startActivityForResult(model.exception.intent, Constants.AUTH_CODE_REQUEST_CODE)
                    }
                    is GooglePlayServicesAvailabilityIOException -> {
                        showGooglePlayServicesAvailabilityErrorDialog(model.exception.connectionStatusCode)
                    }
                    else -> {
                        viewModel.loadError()
                    }
                }
            }
            is UiModel.Content -> {
                showActionViews()
                adapter.videos = model.videos
            }
            is UiModel.Navigate -> {

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
            startActivityForResult(credential.credential.newChooseAccountIntent(), Constants.REQUEST_ACCOUNT_PICKER)
        } else {
            viewModel.loadError()
        }
    }

    private fun checkGooglePlayServicesAvailable() {
        val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {
            showGooglePlayServicesAvailabilityErrorDialog(resultCode)
        } else {
            viewModel.refresh()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            Constants.REQUEST_ACCOUNT_PICKER -> {
                val accountName = data?.extras?.getString(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    viewModel.saveAccountName(accountName)
                } else {
                    viewModel.loadError()
                }
            }
            Constants.AUTH_CODE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.refresh()
                } else {
                    viewModel.loadError()
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