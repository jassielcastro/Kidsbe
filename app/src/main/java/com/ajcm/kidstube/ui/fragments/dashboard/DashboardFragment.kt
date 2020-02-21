package com.ajcm.kidstube.ui.fragments.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ajcm.kidstube.R
import kotlinx.android.synthetic.main.dashboard_fragment.*
import com.ajcm.kidstube.ui.fragments.dashboard.DashboardViewModel.UiModel
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment(R.layout.dashboard_fragment) {

    private val viewModel: DashboardViewModel by currentScope.viewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.userToken = arguments?.getString("user_token")
        viewModel.model.observe(this, Observer(::updateUi))
    }

    private fun updateUi(model: UiModel) {

        progress.visibility = if (model is UiModel.Loading) View.VISIBLE else View.GONE

        when (model) {
            is UiModel.Content -> {
                results.text = "Videos encontrados: ${model.videos.size}"
                model.videos.map {
                    println("DashboardFragment.updateUi --> ${it.toString()}")
                }
            }
            is UiModel.Navigate -> {

            }
        }
    }
}