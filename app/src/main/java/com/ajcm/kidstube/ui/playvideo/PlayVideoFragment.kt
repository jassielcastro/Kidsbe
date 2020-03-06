package com.ajcm.kidstube.ui.playvideo

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.ui.adapters.RelatedVideosAdapter
import com.payclip.design.extensions.hide
import com.payclip.design.extensions.setUpLayoutManager
import com.payclip.design.extensions.show
import com.payclip.design.youtubuplayer.player.YouTubePlayer
import com.payclip.design.youtubuplayer.player.listeners.AbstractYouTubePlayerListener
import com.payclip.design.youtubuplayer.player.options.PanelState
import com.payclip.design.youtubuplayer.player.utils.loadOrCueVideo
import kotlinx.android.synthetic.main.play_video_fragment.*
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class PlayVideoFragment : KidsFragment<UiPlayVideo, PlayVideoViewModel>(R.layout.play_video_fragment) {

    override val viewModel: PlayVideoViewModel by currentScope.viewModel(this)

    private lateinit var youtubePlayer: YouTubePlayer

    private lateinit var adapter: RelatedVideosAdapter

    private var lastPanelState: PanelState = PanelState.COLLAPSED

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dispatch(ActionPlayVideo.ObtainVideo(arguments))
        setUpViews()
        setUpListeners()
    }

    @InternalCoroutinesApi
    private fun setUpListeners() {
        youtube_player_view.setOnPanelListener { panelState ->
            toggleVideoView(panelState)
        }

        youtube_player_view.setOnVideoFinishListener {
            viewModel.dispatch(ActionPlayVideo.PlayNextVideo)
        }

        btnBack.setOnClickListener { activity?.onBackPressed() }
    }

    @InternalCoroutinesApi
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

    @InternalCoroutinesApi
    override fun updateUi(state: UiState) {
        when (state) {
            UiPlayVideo.Loading -> {
                btnBack.hide()
            }
            UiPlayVideo.LoadingError -> {
                btnBack.show()
            }
            is UiPlayVideo.RenderYoutubePlayer -> {
                songTrackListener?.enableSong(false)
                songTrackListener?.onPauseSong()
                youtubePlayer = state.youTubePlayer
                viewModel.dispatch(ActionPlayVideo.Start)
            }
            is UiPlayVideo.Content -> {
                adapter.videos = state.videos
            }
            is UiPlayVideo.PlayVideo -> {
                btnBack.show()
                viewPanelLoader.hide()
                progressLoader.hide()
                viewModel.dispatch(ActionPlayVideo.SaveLastVideoId(state.videoId))
                youtube_player_view.loadThumbnailImage(state.thumbnail)
                youtubePlayer.loadOrCueVideo(lifecycle, state.videoId, 0f)
                viewModel.dispatch(ActionPlayVideo.Refresh)
            }
        }
    }

    private fun toggleVideoView(state: PanelState) {
        lastPanelState = state
        videoBottomGuideline?.let { guideLine ->
            val params = guideLine.layoutParams as ConstraintLayout.LayoutParams

            when (state) {
                PanelState.EXPAND -> btnBack.hide()
                PanelState.COLLAPSED -> btnBack.show()
            }

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
        }
    }

}