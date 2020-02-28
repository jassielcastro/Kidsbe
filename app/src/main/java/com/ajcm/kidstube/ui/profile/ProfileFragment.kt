package com.ajcm.kidstube.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.payclip.design.extensions.loadRes
import com.payclip.design.extensions.setUpLayoutManager
import com.payclip.design.extensions.waitForTransition
import kotlinx.android.synthetic.main.profile_fragment.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : KidsFragment<UiProfile, ProfileViewModel>(R.layout.profile_fragment), View.OnClickListener {

    override val viewModel: ProfileViewModel by currentScope.viewModel(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
        btnBack.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpViews() {
        waitForTransition(imgProfile)

        imgProfile.loadRes(R.drawable.bck_avatar_kids_mia)
        txtUserName.text = "Jahaziel"

        recyclerAvatars.setUpLayoutManager()
    }

    override fun updateUi(state: UiState) {

    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.btnBack -> activity?.onBackPressed()
        }
    }

}