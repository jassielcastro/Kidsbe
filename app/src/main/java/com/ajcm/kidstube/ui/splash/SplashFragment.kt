package com.ajcm.kidstube.ui.splash

import android.Manifest
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.common.Constants
import com.ajcm.design.extensions.accelerateCanvas
import com.ajcm.design.extensions.navigateTo
import kotlinx.android.synthetic.main.splash_fragment.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class SplashFragment : KidsFragment<UiSplash, SplashViewModel>(R.layout.splash_fragment) {

    override val viewModel: SplashViewModel by currentScope.viewModel(this) {
        parametersOf(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onPlaySong(songTrackListener)

        animationSplash.accelerateCanvas()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(songTrackListener)
    }

    override fun updateUi(state: UiState) {
        when (state) {
            is UiSplash.Loading -> {
                viewModel.dispatch(ActionSplash.RequestPermissions)
            }
            UiSplash.CheckPermissions -> {
                if (permissionsGranted()) {
                    viewModel.dispatch(ActionSplash.ValidateAccount)
                } else {
                    requestPermissions(arrayOf(Manifest.permission.GET_ACCOUNTS), Constants.AUTH_CODE_REQUEST_CODE)
                }
            }
            is UiSplash.LoadingError -> {

            }
            is UiSplash.RequestAccount -> {
                stopAnimation()
                startActivityForResult(credential.credential.newChooseAccountIntent(), Constants.REQUEST_ACCOUNT_PICKER)
            }
            is UiSplash.Navigate -> {
                val userBundle = Bundle().apply {
                    putString(Constants.KEY_USER_ID, state.userId)
                }
                navigateTo(R.id.action_splashFragment_to_dashboardFragment, bundle = userBundle)
            }
        }
    }

    private fun permissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.GET_ACCOUNTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startAnimation() {
        animationSplash.playAnimation()
    }

    private fun stopAnimation() {
        animationSplash.cancelAnimation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.AUTH_CODE_REQUEST_CODE -> {
                updateUi(UiSplash.CheckPermissions)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_ACCOUNT_PICKER -> {
                val accountName = data?.extras?.getString(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    credential.credential.selectedAccountName = accountName
                    startAnimation()
                    viewModel.dispatch(ActionSplash.SaveAccount(accountName))
                } else {
                    viewModel.dispatch(ActionSplash.LoadError)
                }
            }

        }
    }

}