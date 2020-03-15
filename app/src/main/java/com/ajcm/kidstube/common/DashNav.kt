package com.ajcm.kidstube.common

import androidx.annotation.IdRes
import com.ajcm.kidstube.R

enum class DashNav(@IdRes val id: Int) {
    PROFILE(R.id.action_dashboardFragment_to_profileFragment),
    SETTINGS(R.id.action_dashboardFragment_to_settingsFragment),
    SEARCH(R.id.action_dashboardFragment_to_searchFragment),
    VIDEO(R.id.action_dashboardFragment_to_playVideoFragment),
    SEARCH_TO_VIDEO(R.id.action_searchFragment_to_playVideoFragment)
}