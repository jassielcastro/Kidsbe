package com.ajcm.kidstube.ui.fragments.splash

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ajcm.kidstube.R
import org.koin.android.scope.currentScope
import com.ajcm.kidstube.ui.fragments.splash.SplashViewModel.UiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SplashFragment : Fragment(R.layout.splash_fragment) {

    private val viewModel: SplashViewModel by currentScope.viewModel(this) {
        parametersOf(this)
    }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.model.observe(this, Observer(::updateUi))
    }

    private fun updateUi(model: UiModel) {
        when (model) {
            is UiModel.Loading -> {

            }
            is UiModel.Navigate -> {
                println("SplashFragment.updateUi --> ${model.userToken}")
                findNavController().navigate(
                    R.id.action_splashFragment_to_dashboardFragment,
                    Bundle().apply {
                        putString("user_token", model.userToken)
                    })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SplashViewModel.RC_SIGN_IN -> {
                viewModel.getSignInResultFromIntent(data)
            }
            SplashViewModel.RC_RECOVERABLE -> {
                if (resultCode == RESULT_OK) {
                    viewModel.continueSplash()
                }
            }
        }
    }

}