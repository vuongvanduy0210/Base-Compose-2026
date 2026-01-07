package com.duyvv.basecompose.presentation.ui.main

import com.duyvv.basecompose.presentation.base.MVIEffect
import com.duyvv.basecompose.presentation.base.MVIIntent
import com.duyvv.basecompose.presentation.base.UiState

data class MainUiState(
    val x: Int = 0
) : UiState

sealed interface MainIntent : MVIIntent

sealed interface MainEffect : MVIEffect