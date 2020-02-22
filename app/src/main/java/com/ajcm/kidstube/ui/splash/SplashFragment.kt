package com.ajcm.kidstube.ui.splash

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ajcm.data.auth.Constants
import com.ajcm.kidstube.R
import com.ajcm.kidstube.common.GoogleCredential
import com.ajcm.kidstube.extensions.navigateTo
import com.ajcm.kidstube.ui.splash.SplashViewModel.UiModel
import kotlinx.android.synthetic.main.splash_fragment.*
import org.koin.android.ext.android.inject
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SplashFragment : Fragment(R.layout.splash_fragment) {

    private val viewModel: SplashViewModel by currentScope.viewModel(this) {
        parametersOf(this)
    }

    private val credential: GoogleCredential by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.model.observe(this, Observer(::updateUi))
    }

    private fun updateUi(model: UiModel) {
        stopAnimation()
        when (model) {
            is UiModel.Loading -> {
                startAnimation()
            }
            is UiModel.LoadingError -> {

            }
            is UiModel.RequestAccount -> {
                startActivityForResult(credential.credential.newChooseAccountIntent(), Constants.REQUEST_ACCOUNT_PICKER)
            }
            is UiModel.Navigate -> {
                navigateTo(R.id.action_splashFragment_to_dashboardFragment)
            }
        }
    }

    private fun startAnimation() {
        animationWaves.playAnimation()
        animationSplash.playAnimation()
    }

    private fun stopAnimation() {
        animationWaves.cancelAnimation()
        animationSplash.cancelAnimation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_ACCOUNT_PICKER -> {
                val accountName = data?.extras?.getString(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    viewModel.saveAccountName(accountName)
                } else {
                    viewModel.loadError()
                }
            }
        }
    }

}