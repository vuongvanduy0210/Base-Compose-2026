package com.duyvv.basecompose.presentation.ui.lfo

import android.app.Activity
import android.content.Context
import com.duyvv.basecompose.domain.model.Language
import com.duyvv.basecompose.presentation.base.MVIEffect
import com.duyvv.basecompose.presentation.base.MVIIntent
import com.duyvv.basecompose.presentation.base.UiState

data class LFOUiState(
    val listLanguage: List<Language> = emptyList(),
    val isShowNativeLFO2: Boolean = false,
) : UiState

sealed interface LFOIntent : MVIIntent {
    data object InitializeData : LFOIntent
    data class SelectLanguage(val language: Language, val isShowNativeLFO2: Boolean) : LFOIntent
    data class RequestNativeLFO2(val activity: Activity) : LFOIntent
    data class RequestNativeOnboarding(val activity: Activity) : LFOIntent
    data class ApplySelectedLanguage(val context: Context) : LFOIntent
}

sealed interface LFOEffect : MVIEffect {
    object NavigateNextScreen : LFOEffect
}