package com.duyvv.basecompose.presentation.ui.splash

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.duyvv.basecompose.presentation.base.MVIEffect
import com.duyvv.basecompose.presentation.base.MVIIntent
import com.duyvv.basecompose.presentation.base.UiState

data class SplashUiState(
    val interRequestCompleted: Boolean = false,
    val bannerRequestCompleted: Boolean = false,
    val isShowBanner: Boolean = false,
) : UiState

sealed interface SplashIntent : MVIIntent {
    data class Initialize(val activity: Activity, val owner: LifecycleOwner) : SplashIntent

    object BannerRequestCompleted : SplashIntent
    data class PreloadAds(val activity: Activity) : SplashIntent
    object SplashAdRequestCompleted : SplashIntent
}

sealed interface SplashEffect : MVIEffect {
    object NavigateNextScreen : SplashEffect
    object CallRequestAds : SplashEffect
}