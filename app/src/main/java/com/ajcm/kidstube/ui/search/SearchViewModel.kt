package com.ajcm.kidstube.ui.search

import androidx.lifecycle.LiveData
import com.ajcm.kidstube.arch.ActionState
import com.ajcm.kidstube.common.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher

class SearchViewModel(uiDispatcher: CoroutineDispatcher): ScopedViewModel<UiSearch>(uiDispatcher) {

    override val model: LiveData<UiSearch>
        get() {
            if (_model.value == null) dispatch(ActionSearch.None)
            return _model
        }

    init {
        initScope()
    }

    override fun dispatch(actionState: ActionState) {

    }

}