package com.ajcm.kidstube.ui.search

import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class SearchFragment : KidsFragment<UiSearch, SearchViewModel>(R.layout.search_fragment) {

    override val viewModel: SearchViewModel by currentScope.viewModel(this)

    override val sound: Int
        get() = R.raw.dashboard_ben_smile

    override fun updateUi(state: UiState) {

    }

}