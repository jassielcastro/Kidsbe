package com.ajcm.kidstube.ui.profile

import android.os.Bundle
import android.view.View
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.extensions.getDrawable
import com.ajcm.kidstube.ui.adapters.AvatarAdapter
import com.payclip.design.extensions.loadRes
import com.payclip.design.extensions.setUpLayoutManager
import com.payclip.design.extensions.waitForTransition
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : KidsFragment<UiProfile, ProfileViewModel>(R.layout.profile_fragment), View.OnClickListener {

    override val viewModel: ProfileViewModel by currentScope.viewModel(this)

    private lateinit var adapter: AvatarAdapter

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
        btnBack.setOnClickListener(this)
    }

    @InternalCoroutinesApi
    private fun setUpViews() {
        waitForTransition(imgProfile)
        recyclerAvatars.setUpLayoutManager()

        adapter = AvatarAdapter {
            viewModel.dispatch(ActionProfile.AvatarSelected(it))
        }

        recyclerAvatars.adapter = adapter
    }

    @InternalCoroutinesApi
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