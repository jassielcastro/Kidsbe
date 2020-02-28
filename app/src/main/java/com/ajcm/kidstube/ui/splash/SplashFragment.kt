package com.ajcm.kidstube.ui.splash

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.common.Constants
import com.payclip.design.extensions.accelerateCanvas
import com.payclip.design.extensions.navigateTo
import kotlinx.android.synthetic.main.splash_fragment.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SplashFragment : KidsFragment<UiSplash, SplashViewModel>(R.layout.splash_fragment) {

    override val viewModel: SplashViewModel by currentScope.viewModel(this) {
        parametersOf(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animationWaves.accelerateCanvas()
        animationSplash.accelerateCanvas()
    }

    override fun updateUi(state: UiState) {
        stopAnimation()
        when (state) {
            is UiSplash.Loading -> {
                startAnimation()
            }
            is UiSplash.LoadingError -> {

            }
            is UiSplash.RequestAccount -> {
                startActivityForResult(credential.credential.newChooseAccountIntent(), Constants.REQUEST_ACCOUNT_PICKER)
            }
            is UiSplash.Navigate -> {
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
                    viewModel.dispatch(ActionSplash.SaveAccount(accountName))
                } else {
                    viewModel.dispatch(ActionSplash.LoadError)
                }
            }
        }
    }

}