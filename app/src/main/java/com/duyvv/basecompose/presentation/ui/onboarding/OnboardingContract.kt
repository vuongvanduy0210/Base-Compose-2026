package com.duyvv.basecompose.presentation.ui.onboarding

import android.app.Activity
import com.duyvv.basecompose.presentation.base.MVIEffect
import com.duyvv.basecompose.presentation.base.MVIIntent
import com.duyvv.basecompose.presentation.base.UiState

data class OnboardingUiState(
    val totalPage: Int = 4,
    val currentPagePos: Int = 0,
    val shouldShowNativeFull23: Boolean = true
) : UiState

sealed interface OnboardingIntent : MVIIntent {
    data class SelectPage(val position: Int, val activity: Activity?) : OnboardingIntent
    data class UpdateTotalPage(val totalPage: Int) : OnboardingIntent
    data object RemoveNativeFull23Page : OnboardingIntent
}

sealed interface OnboardingEffect : MVIEffect