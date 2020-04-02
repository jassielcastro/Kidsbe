package com.ajcm.kidstube.ui.playvideo

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.common.Constants
import com.ajcm.kidstube.ui.adapters.RelatedVideosAdapter
import com.ajcm.design.extensions.*
import com.ajcm.design.youtubuplayer.player.YouTubePlayer
import com.ajcm.design.youtubuplayer.player.listeners.AbstractYouTubePlayerListener
import com.ajcm.design.youtubuplayer.player.options.PanelState
import com.ajcm.design.youtubuplayer.player.utils.loadOrCueVideo
import kotlinx.android.synthetic.main.item_video_options.*
import kotlinx.android.synthetic.main.play_video_fragment.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayVideoFragment : KidsFragment<UiPlayVideo, PlayVideoViewModel>(R.layout.play_video_fragment) {

    override val viewModel: PlayVideoViewModel by currentScope.viewModel(this) {
        parametersOf(arguments?.getString(Constants.KEY_USER_ID, "unknown"))
    }

    private lateinit var youtubePlayer: YouTubePlayer

    private lateinit var adapter: RelatedVideosAdapter

    private var lastPanelState: PanelState = PanelState.COLLAPSED

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
        setUpListeners()
    }

    private fun setUpListeners() {
        youtube_player_view.setOnPanelListener { panelState ->
            toggleVideoView(panelState)
        }

        youtube_player_view.setOnVideoFinishListener {
            viewModel.dispatch(ActionPlayVideo.PlayNextVideo)
        }

        btnBack.setOnClickListener { activity?.onBackPressed() }

        btnOptions.setOnClickListener {
            handleVideoOptions()
        }

        btnVideoBlock.setOnClickListener {
            viewModel.dispatch(ActionPlayVideo.BlockCurrentVideo)
        }

        btnVideoRelated.setOnClickListener {
            viewModel.dispatch(ActionPlayVideo.LoadVideosByCurrent)
        }

        relatedRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    SCROLL_STATE_IDLE -> {
                        canResizeContent(true)
                    }
                    else -> {
                        canResizeContent(false)
                    }
                }
            }
        })
    }

    private fun handleVideoOptions() {
        if (videoOptions.isVisible) {
            videoOptions.hide()
            canResizeContent(true)
        } else {
            videoOptions.show()
            canResizeContent(false)
        }
    }

    private fun setUpViews() {
        relatedRecycler.setUpLayoutManager()
        adapter = RelatedVideosAdapter {
            viewModel.dispatch(ActionPlayVideo.VideoSelected(it))
        }
        relatedRecycler.adapter = adapter

        lifecycle.addObserver(youtube_player_view)
        youtube_player_view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                viewModel.dispatch(ActionPlayVideo.PlayerViewReady(youTubePlayer))
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (lastPanelState == PanelState.EXPAND) {
            youtube_player_view.onResume()
        }
    }

    override fun onStop() {
        youtube_player_view.onStop()
        super.onStop()
    }

    override fun updateUi(state: UiState) {
        println("PlayVideoFragment.updateUi --> $state")
        when (state) {
            UiPlayVideo.Loading -> {
                btnBack.hide()
                btnOptions.hide()
                videoOptions.hide()
                viewModel.dispatch(ActionPlayVideo.ObtainVideo(arguments))
            }
            UiPlayVideo.LoadingError -> {
                btnBack.show()
                btnOptions.hide()
                videoOptions.hide()
            }
            is UiPlayVideo.RenderYoutubePlayer -> {
                viewModel.stopAppMusic(songTrackListener)
                youtubePlayer = state.youTubePlayer
                viewModel.dispatch(ActionPlayVideo.Start)
            }
            is UiPlayVideo.Content -> {
                videoOptions.hide()
                canResizeContent(true)
                showViewReady()
                adapter.videos = state.videos
                viewModel.dispatch(ActionPlayVideo.ComputeScroll)
            }
            is UiPlayVideo.PlayVideo -> {
                showViewReady()
                youtube_player_view.loadThumbnailImage(state.video.thumbnail)
                youtubePlayer.loadOrCueVideo(lifecycle, state.video.videoId, 0f)
                viewModel.dispatch(ActionPlayVideo.Refresh)
            }
            is UiPlayVideo.ScrollToVideo -> {
                relatedRecycler?.post {
                    relatedRecycler.setSmoothScroll(state.position)
                    canResizeContent(true)
                }
            }
        }
    }

    private fun showViewReady() {
        btnBack.show()
        btnOptions.show()
        viewPanelLoader.hide()
        progressLoader.hide()
    }

    private fun toggleVideoView(state: PanelState) {
        lastPanelState = state
        videoBottomGuideline?.let { guideLine ->
            val params = guideLine.layoutParams as ConstraintLayout.LayoutParams

            val start = if (state != PanelState.COLLAPSED) 0.7f else 1f
            val end = if (state == PanelState.COLLAPSED) 0.7f else 1f

            val valueAnimator = ValueAnimator.ofFloat(start, end)

            valueAnimator.addUpdateListener {
                params.guidePercent = it.animatedValue as Float
                guideLine.layoutParams = params
            }

            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.duration = 220

            valueAnimator.start()

            hideOrShowViews(state)
        }
    }

    private fun hideOrShowViews(state: PanelState) {
        when (state) {
            PanelState.EXPAND -> {
                btnBack.hide()
                btnOptions.hide()
                relatedRecycler.hide()
            }
            PanelState.COLLAPSED -> {
                btnBack.show()
                btnOptions.show()
                relatedRecycler.show()
            }
        }
    }

    private fun canResizeContent(resize: Boolean) {
        youtube_player_view?.canHideContent(resize)
    }

}