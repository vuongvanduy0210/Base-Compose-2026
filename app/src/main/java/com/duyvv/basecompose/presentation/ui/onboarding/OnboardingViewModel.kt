package com.duyvv.basecompose.presentation.ui.onboarding

import android.app.Activity
import androidx.compose.runtime.Stable
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.presentation.base.ComposeViewModel
import com.duyvv.basecompose.utils.InterAdManager
import com.duyvv.basecompose.utils.NativeAdManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
@Stable
class OnboardingViewModel :
    ComposeViewModel<OnboardingUiState, OnboardingIntent, OnboardingEffect>() {

    init {
        AppConfigManager.getInstance().isShowNativeOnboard.asFlow.mapLatest {
            updateUiState {
                copy(configEnableNativeOnboard = it)
            }
        }.launchIn(viewModelSafetyScope)

        AppConfigManager.getInstance().configNativeOB.asFlow.mapLatest {
            updateUiState {
                copy(isShowNativeBig = it == "medium")
            }
        }.launchIn(viewModelSafetyScope)


        AppConfigManager.getInstance().disableBack.asFlow.mapLatest {
            updateUiState {
                copy(disableBack = it)
            }
        }.launchIn(viewModelSafetyScope)

        combine(
            AppConfigManager.getInstance().isShowNativeOB12FullScreen.asFlow,
            AppConfigManager.getInstance().isShowNativeOB23FullScreen.asFlow
        ) { isEnableF12, isEnable23 ->
            updateUiState {
                val newPages = generatePageList(
                    enable12 = isEnableF12,
                    enable23 = isEnable23
                )
                copy(pages = newPages)
            }
        }.launchIn(viewModelSafetyScope)
    }

    var isInterObLoaded = false

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

            OnboardingIntent.RemoveNativeFull12Page -> {
                updateUiState {
                    val newPages = uiStateValue.pages.toMutableList().apply {
                        remove(OnboardingPageType.NATIVE_FULL_12)
                    }
                    copy(pages = newPages)
                }
            }

            OnboardingIntent.RemoveNativeFull23Page -> {
                updateUiState {
                    val newPages = uiStateValue.pages.toMutableList().apply {
                        remove(OnboardingPageType.NATIVE_FULL_23)
                    }
                    copy(pages = newPages)
                }
            }
        }
    }

    private fun preloadAdsForNextPage(currentPagePos: Int, activity: Activity?) {
        val pages = uiStateValue.pages
        val nextPosition = currentPagePos + 1
        if (nextPosition >= pages.size - 1) {
            if (!isInterObLoaded) {
                activity?.let {
                    isInterObLoaded = true
                    InterAdManager.requestInter(
                        activity = it,
                        adPlacement = InterAdManager.INTER_ONBOARD
                    )
                }
            }
            return
        }

        val nextPageType = pages.getOrNull(nextPosition)
        when (nextPageType) {
            OnboardingPageType.NATIVE_FULL_12 -> {
                activity?.let { NativeAdManager.requestNativeObFull12(it) }
            }

            OnboardingPageType.NATIVE_FULL_23 -> {
                activity?.let { NativeAdManager.requestNativeObFull23(it) }
            }

            else -> {
            }
        }
    }

    private fun generatePageList(enable12: Boolean, enable23: Boolean): List<OnboardingPageType> {
        val list = mutableListOf<OnboardingPageType>()
        list.add(OnboardingPageType.PAGE_1)
        if (enable12) {
            list.add(OnboardingPageType.NATIVE_FULL_12)
        }
        list.add(OnboardingPageType.PAGE_2)
        if (enable23) {
            list.add(OnboardingPageType.NATIVE_FULL_23)
        }
        list.add(OnboardingPageType.PAGE_3)
        return list
    }
}

enum class OnboardingPageType {
    PAGE_1,
    NATIVE_FULL_12,
    PAGE_2,
    NATIVE_FULL_23,
    PAGE_3
}