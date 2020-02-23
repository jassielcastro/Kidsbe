package com.ajcm.kidstube.arch

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ajcm.kidstube.common.GoogleCredential
import com.ajcm.kidstube.common.ScopedViewModel
import org.koin.android.ext.android.inject

abstract class KidsFragment<UI: UiState, VM: ScopedViewModel<UI>>(@LayoutRes layout: Int): Fragment(layout) {

    abstract val viewModel: VM
    val credential: GoogleCredential by inject()

    abstract fun updateUi(state: UiState)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.model.observe(this, Observer(::updateUi))
    }

}