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

        addListeners()
    }

    private fun addListeners() {
        btnBack.setOnClickListener(this)

        switchAppSounds.setOnCheckedChangeListener { _, checked ->
            viewModel.dispatch(ActionSettings.UpdateAppSoundsEnabled(checked))
        }
        switchEffects.setOnCheckedChangeListener { _, checked ->
            viewModel.dispatch(ActionSettings.UpdateAppEffectEnabled(checked))
        }
    }

    override fun updateUi(state: UiState) {
        when (state) {
            is UiSettings.LoadUserInfo -> {
                imgProfile.loadRes(state.user.userAvatar.getDrawable())
                txtUserName.text = state.user.userName
                switchAppSounds.isChecked = state.user.appEffect
                switchEffects.isChecked = state.user.soundEffect
                viewModel.onHandleMusicApp(state.user, songTrackListener)
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnBack -> activity?.onBackPressed()
        }
    }

}