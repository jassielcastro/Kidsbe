package com.ajcm.kidstube.ui.profile

import android.os.Bundle
import android.view.View
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.extensions.getDrawable
import com.ajcm.kidstube.ui.adapters.AvatarAdapter
import com.ajcm.design.extensions.loadRes
import com.ajcm.design.extensions.setUpLayoutManager
import com.ajcm.design.extensions.waitForTransition
import kotlinx.android.synthetic.main.profile_fragment.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : KidsFragment<UiProfile, ProfileViewModel>(R.layout.profile_fragment), View.OnClickListener {

    override val viewModel: ProfileViewModel by currentScope.viewModel(this)

    private lateinit var adapter: AvatarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
        btnBack.setOnClickListener(this)
    }

    private fun setUpViews() {
        waitForTransition(imgProfile)
        recyclerAvatars.setUpLayoutManager()

        adapter = AvatarAdapter {
            viewModel.dispatch(ActionProfile.AvatarSelected(it))
        }

        recyclerAvatars.adapter = adapter
    }

    override fun updateUi(state: UiState) {
        when(state) {
            is UiProfile.UpdateUserInfo -> {
                imgProfile.loadRes(state.user.userAvatar.getDrawable())
                txtUserName.text = state.user.userName
                viewModel.dispatch(ActionProfile.PrepareAvatarList)
            }
            is UiProfile.AvatarContent -> {
                adapter.avatarList = state.avatarList
            }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.btnBack -> activity?.onBackPressed()
        }
    }

}