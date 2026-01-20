package com.duyvv.basecompose.presentation.ui.onboarding

import android.app.Activity
import com.duyvv.basecompose.presentation.base.MVIEffect
import com.duyvv.basecompose.presentation.base.MVIIntent
import com.duyvv.basecompose.presentation.base.UiState

data class OnboardingUiState(
    val pages: List<OnboardingPageType> = emptyList(),
    val currentPagePos: Int = 0,
    val configEnableNativeOnboard: Boolean? = null,
    val isShowNativeBig: Boolean? = null,
    val nativeLayoutRes: Int? = null,
    val disableBack: Boolean? = null,
) : UiState

sealed interface OnboardingIntent : MVIIntent {
    data class SelectPage(val position: Int, val activity: Activity?) : OnboardingIntent
    data object RemoveNativeFull12Page : OnboardingIntent
    data object RemoveNativeFull23Page : OnboardingIntent
}

sealed interface OnboardingEffect : MVIEffect