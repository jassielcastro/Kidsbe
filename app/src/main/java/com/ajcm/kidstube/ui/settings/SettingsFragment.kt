package com.ajcm.kidstube.ui.settings

import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : KidsFragment<UiSettings, SettingsViewModel>(R.layout.settings_fragment) {

    override val viewModel: SettingsViewModel by currentScope.viewModel(this)

    override fun updateUi(state: UiState) {

    }

}