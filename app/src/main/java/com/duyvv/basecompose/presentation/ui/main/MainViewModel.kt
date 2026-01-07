package com.duyvv.basecompose.presentation.ui.main

import com.duyvv.basecompose.presentation.base.ComposeViewModel

class MainViewModel : ComposeViewModel<MainUiState, MainIntent, MainEffect>() {

    override fun createInitialState(): MainUiState {
        return MainUiState()
    }

    override fun sendIntent(intent: MainIntent) {
        /*when (intent) {

        }*/
    }
}