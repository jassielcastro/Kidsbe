package com.ajcm.kidstube.ui.profile

import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : KidsFragment<UiProfile, ProfileViewModel>(R.layout.profile_fragment) {

    override val viewModel: ProfileViewModel by currentScope.viewModel(this)

    override fun updateUi(state: UiState) {

    }

}