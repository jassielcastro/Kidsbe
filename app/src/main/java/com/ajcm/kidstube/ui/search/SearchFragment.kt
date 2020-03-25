package com.ajcm.kidstube.ui.search

import android.os.Bundle
import android.view.View
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.common.DashNav
import com.ajcm.kidstube.ui.adapters.VideoAdapter
import com.payclip.design.extensions.*
import kotlinx.android.synthetic.main.generic_error.*
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class SearchFragment : KidsFragment<UiSearch, SearchViewModel>(R.layout.search_fragment) {

    override val viewModel: SearchViewModel by currentScope.viewModel(this)

    private lateinit var adapter: VideoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
        addListener()
        stopLoadingAnim()
    }

    override fun updateUi(state: UiState) {
        contentError.hide()
        when(state) {
            UiSearch.Start -> {
                viewModel.dispatch(ActionSearch.PrepareYoutube)
            }
            is UiSearch.Content -> {
                stopLoadingAnim()
                searchResults.show()
                adapter.videos = state.videos
            }
            UiSearch.Loading -> {
                startLoadingAnim()
            }
            UiSearch.LoadError -> {
                stopLoadingAnim()
                contentError.show()
            }
            is UiSearch.NavigateTo -> {
                if (state.root == DashNav.SEARCH_TO_VIDEO) {
                    navigateTo(state.root.id, Bundle().apply {
                        putString(Constants.KEY_USER_ID, state.userId)
                        putSerializable(Constants.KEY_VIDEO_ID, state.video!!)
                    })
                }
            }
        }
    }

    private fun setUpViews() {
        txtErrorTitle.text = getString(R.string.try_searching_other)

        adapter = VideoAdapter(false) { video ->
            viewModel.dispatch(ActionSearch.VideoSelected(video))
        }

        searchResults.setUpLayoutManager()
        searchResults.adapter = adapter
    }

    private fun addListener() {
        btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        searchTextBox.setEndIconOnClickListener {
            searchTextBox.clear()
        }

        searchTextBox.setOnActionClickListener({
            restoreFullScreen()
            viewModel.dispatch(ActionSearch.Search(it))
        }, {
            restoreFullScreen()
        })
    }

    private fun restoreFullScreen() {
        activity?.fullScreen()
    }

    private fun stopLoadingAnim() {
        progress.pauseAnimation()
        progress.hide()
    }

    private fun startLoadingAnim() {
        searchResults.hide()
        progress.show()
        progress.playAnimation()
    }

}