package com.ajcm.kidstube.ui.fragments.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ajcm.kidstube.R
import com.ajcm.kidstube.ui.fragments.splash.SplashViewModel.UiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.scope.currentScope
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
                findNavController().navigate(R.id.action_splashFragment_to_dashboardFragment)
            }
        }
    }

}