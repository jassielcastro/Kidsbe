package com.ajcm.kidstube.ui.playvideo

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.extensions.hide
import com.ajcm.kidstube.extensions.setUpLayoutManager
import com.ajcm.kidstube.ui.adapters.RelatedVideosAdapter
import com.ajcm.kidstube.ui.playvideo.customview.CustomPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import kotlinx.android.synthetic.main.play_video_fragment.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class PlayVideoFragment : KidsFragment<UiPlayVideo, PlayVideoViewModel>(R.layout.play_video_fragment) {

    override val viewModel: PlayVideoViewModel by currentScope.viewModel(this)
    private lateinit var youtubePlayer: YouTubePlayer

    private lateinit var customPlayerUi : ViewGroup
    private lateinit var adapter: RelatedVideosAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dispatch(ActionPlayVideo.ObtainVideo(arguments))
        setUpViews()
    }

    private fun setUpViews() {
        relatedRecycler.setUpLayoutManager()
        adapter = RelatedVideosAdapter {
            viewModel.dispatch(ActionPlayVideo.VideoSelected(it))
        }
        relatedRecycler.adapter = adapter

        customPlayerUi = youtube_player_view.inflateCustomPlayerUi(R.layout.custom_player_ui) as ViewGroup
        lifecycle.addObserver(youtube_player_view)
        youtube_player_view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                viewModel.dispatch(ActionPlayVideo.PlayerViewReady(youTubePlayer))
            }
        })
    }

    override fun updateUi(state: UiState) {
        when (state) {
            UiPlayVideo.Loading -> {

            }
            UiPlayVideo.LoadingError -> {

            }
            is UiPlayVideo.RenderYoutubePlayer -> {
                youtubePlayer = state.youTubePlayer
                val customPlayer = CustomPlayerUiController(customPlayerUi, youtubePlayer, { panelState ->
                    toggleVideoView(panelState)
                }, {
                    viewModel.dispatch(ActionPlayVideo.PlayNextVideo)
                })
                customPlayer.onReady(youtubePlayer)
                youtubePlayer.addListener(customPlayer)

                viewModel.dispatch(ActionPlayVideo.Start)
            }
            is UiPlayVideo.Content -> {
                adapter.videos = state.videos
            }
            is UiPlayVideo.PlayVideo -> {
                viewPanelLoader.hide()
                progressLoader.hide()
                viewModel.dispatch(ActionPlayVideo.SaveLastVideoId(state.videoId))
                youtubePlayer.loadOrCueVideo(lifecycle, state.videoId, 0f)
                viewModel.dispatch(ActionPlayVideo.Refresh)
            }
        }
    }

    private fun toggleVideoView(state: CustomPlayerUiController.PanelState) {
        val guideLine = videoBottomGuideline
        val params = guideLine.layoutParams as ConstraintLayout.LayoutParams

        val start = if (state != CustomPlayerUiController.PanelState.COLLAPSED) 0.7f else 1f
        val end = if (state == CustomPlayerUiController.PanelState.COLLAPSED) 0.7f else 1f

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