package com.ajcm.kidstube.ui.playvideo

import android.os.Bundle
import android.view.View
import com.ajcm.kidstube.R
import com.ajcm.kidstube.arch.KidsFragment
import com.ajcm.kidstube.arch.UiState
import com.ajcm.kidstube.extensions.hide
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

    private val customPlayerUi : View by lazy {
        youtube_player_view.inflateCustomPlayerUi(R.layout.custom_player_ui)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
    }

    private fun setUpViews() {
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
                viewModel.dispatch(ActionPlayVideo.Start(arguments))

                val customPlayer = CustomPlayerUiController(customPlayerUi, youtubePlayer)
                youtubePlayer.addListener(customPlayer)

                viewModel.dispatch(ActionPlayVideo.Refresh)
            }
            is UiPlayVideo.Content -> {

            }
            is UiPlayVideo.PlayVideo -> {
                viewFrame.hide()
                progressLoader.hide()
                viewModel.dispatch(ActionPlayVideo.SaveLastVideoId(state.videoId))
                youtubePlayer.loadOrCueVideo(lifecycle, state.videoId, 0f)
            }
        }
    }

}