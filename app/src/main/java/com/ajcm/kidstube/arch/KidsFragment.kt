package com.ajcm.kidstube.arch

import android.media.MediaPlayer
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import com.ajcm.kidstube.R
import com.ajcm.kidstube.common.GoogleCredential
import com.ajcm.kidstube.common.ScopedViewModel
import org.koin.android.ext.android.inject

abstract class KidsFragment<UI: UiState, VM: ScopedViewModel<UI>>(@LayoutRes layout: Int): Fragment(layout) {

    companion object {
        const val NO_SOUND: Int = -1
    }

    abstract val viewModel: VM

    abstract val sound: Int

    private var isReadyToPlay = false

    val credential: GoogleCredential by inject()

    private val mediaPlayer: MediaPlayer? by lazy {
        if (sound != NO_SOUND) MediaPlayer.create(requireContext(), sound) else null
    }

    abstract fun updateUi(state: UiState)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TransitionInflater
            .from(context)
            .inflateTransition(R.transition.image_shared_element_transition).let {
                sharedElementEnterTransition = it
                sharedElementReturnTransition = it
            }

        viewModel.model.observe(this, Observer(::updateUi))

        mediaPlayer?.setOnPreparedListener {
            it.start()
            isReadyToPlay = true
        }

        mediaPlayer?.setOnCompletionListener {
            it.seekTo(0)
            it.start()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mediaPlayer?.isPlaying == false && isReadyToPlay) {
            mediaPlayer?.start()
        }
    }

    override fun onPause() {
        mediaPlayer?.pause()
        super.onPause()
    }

}