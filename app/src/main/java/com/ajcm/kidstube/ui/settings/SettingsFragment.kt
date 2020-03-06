package com.ajcm.kidstube.ui.settings

import android.os.Bundle
import android.view.View
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.extensions.getDrawable
import com.payclip.design.extensions.loadRes
import kotlinx.android.synthetic.main.settings_fragment.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : KidsFragment<UiSettings, SettingsViewModel>(R.layout.settings_fragment),
    View.OnClickListener {

    override val viewModel: SettingsViewModel by currentScope.viewModel(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener(this)
    }

    override fun updateUi(state: UiState) {
        when (state) {
            is UiSettings.LoadUserInfo -> {
                imgProfile.loadRes(state.user.userAvatar.getDrawable())
                txtUserName.text = state.user.userName
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnBack -> activity?.onBackPressed()
        }
    }

}