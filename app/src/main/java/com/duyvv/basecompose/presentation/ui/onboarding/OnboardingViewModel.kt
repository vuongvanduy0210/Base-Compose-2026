package com.duyvv.basecompose.presentation.ui.onboarding

import android.app.Activity
import com.duyvv.basecompose.presentation.base.ComposeViewModel
import com.duyvv.basecompose.utils.InterAdManager
import com.duyvv.basecompose.utils.NativeAdManager

class OnboardingViewModel :
    ComposeViewModel<OnboardingUiState, OnboardingIntent, OnboardingEffect>() {
    override fun createInitialState(): OnboardingUiState {
        return OnboardingUiState()
    }

    override fun sendIntent(event: OnboardingIntent) {
        when (event) {
            is OnboardingIntent.SelectPage -> {
                updateUiState { copy(currentPagePos = event.position) }
                preloadAdsForNextPage(
                    currentPagePos = event.position,
                    activity = event.activity
                )
            }

            is OnboardingIntent.UpdateTotalPage -> {
                updateUiState { copy(totalPage = event.totalPage) }
            }

            OnboardingIntent.RemoveNativeFull23Page -> {
                updateUiState { copy(totalPage = 3, shouldShowNativeFull23 = false) }
            }
        }
    }

    private fun preloadAdsForNextPage(currentPagePos: Int, activity: Activity?) {
        val nextPosition = currentPagePos + 1
        if (nextPosition >= uiStateValue.totalPage) {
            activity?.let {
                InterAdManager.requestInter(
                    activity = it,
                    adPlacement = InterAdManager.INTER_ONBOARD
                )
            }
            return
        }
        when (nextPosition) {
            2 -> {
                if (uiStateValue.totalPage == 4) {
                    activity?.let { NativeAdManager.requestNativeObFull23(it) }
                }
            }
        }
    }
}