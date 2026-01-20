package com.duyvv.basecompose.presentation.ui.onboarding

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duyvv.basecompose.R
import com.duyvv.basecompose.presentation.common.logEvent
import com.duyvv.basecompose.presentation.ui.onboarding.component.OnboardingFullScreen
import com.duyvv.basecompose.presentation.ui.onboarding.component.OnboardingPageScreen
import com.duyvv.basecompose.utils.NativeAdManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel(),
    onNextScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.pages.size })
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()

    val onNavigateNextScreen = {
        scope.launch {
            if (pagerState.currentPage < uiState.pages.size - 1) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            } else {
                onNextScreen()
            }
        }
    }

    BackHandler {
        if (uiState.disableBack == false) {
            scope.launch { onNavigateNextScreen() }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            viewModel.sendIntent(OnboardingIntent.SelectPage(activity = activity, position = page))
            if (page < uiState.pages.size) {
                val pageType = uiState.pages[page]
                val eventName = when (pageType) {
                    OnboardingPageType.PAGE_1 -> "onboarding1_next_view"
                    OnboardingPageType.NATIVE_FULL_12 -> "onboarding_full_screen12_view"
                    OnboardingPageType.PAGE_2 -> "onboarding2_next_view"
                    OnboardingPageType.NATIVE_FULL_23 -> "onboarding_full_screen23_view"
                    OnboardingPageType.PAGE_3 -> "onboarding3_next_view"
                }
                logEvent(eventName)
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize(),
        beyondViewportPageCount = uiState.pages.size
    ) { index ->
        Log.d("TAG++", "OnboardingScreen: $index")
        val isSelected = pagerState.currentPage == index
        val pageType = uiState.pages.getOrNull(index) ?: return@HorizontalPager
        when (pageType) {
            OnboardingPageType.PAGE_1 -> {
                OnboardingPageScreen(
                    modifier = Modifier.fillMaxSize(),
                    imageResId = R.drawable.img_ob_1,
                    titleResId = R.string.ob1_content,
                    iconSliderResId = R.drawable.ic_slide_1,
                    actionLabelResId = R.string.next,
                    shouldCallRequestNative = isSelected && uiState.configEnableNativeOnboard == true,
                    position = 0,
                    isShowNativeBig = uiState.isShowNativeBig,
                    layoutNativeResId = uiState.nativeLayoutRes,
                    onClickNextAction = {
                        logEvent("onboarding1_next_click")
                        onNavigateNextScreen()
                    }
                )
            }

            OnboardingPageType.NATIVE_FULL_12 -> {
                OnboardingFullScreen(
                    adPlacement = NativeAdManager.NATIVE_OB_FULL_12,
                    modifier = Modifier,
                    isShowNativeAd = isSelected,
                    onNavigateNextScreen = {
                        onNavigateNextScreen()
                    },
                    isShowButtonNext = true,
                    onRemovePage = {
                        viewModel.sendIntent(OnboardingIntent.RemoveNativeFull12Page)
                    }
                )
            }

            OnboardingPageType.PAGE_2 -> {
                OnboardingPageScreen(
                    modifier = Modifier.fillMaxSize(),
                    imageResId = R.drawable.img_ob_2,
                    titleResId = R.string.ob2_content,
                    iconSliderResId = R.drawable.ic_slide_2,
                    actionLabelResId = R.string.next,
                    shouldCallRequestNative = isSelected && uiState.configEnableNativeOnboard == true,
                    position = 1,
                    isShowNativeBig = uiState.isShowNativeBig,
                    layoutNativeResId = uiState.nativeLayoutRes,
                    onClickNextAction = {
                        logEvent("onboarding2_next_click")
                        onNavigateNextScreen()
                    }
                )
            }

            OnboardingPageType.NATIVE_FULL_23 -> {
                OnboardingFullScreen(
                    adPlacement = NativeAdManager.NATIVE_OB_FULL_23,
                    modifier = Modifier,
                    isShowNativeAd = isSelected,
                    onNavigateNextScreen = { onNavigateNextScreen() },
                    isShowButtonNext = true,
                    onRemovePage = {
                        viewModel.sendIntent(OnboardingIntent.RemoveNativeFull23Page)
                    }
                )
            }

            OnboardingPageType.PAGE_3 -> {
                OnboardingPageScreen(
                    modifier = Modifier.fillMaxSize(),
                    imageResId = R.drawable.img_ob_3,
                    titleResId = R.string.ob3_content,
                    iconSliderResId = R.drawable.ic_slide_3,
                    actionLabelResId = R.string.next,
                    shouldCallRequestNative = isSelected && uiState.configEnableNativeOnboard == true,
                    position = 3,
                    isShowNativeBig = uiState.isShowNativeBig,
                    layoutNativeResId = uiState.nativeLayoutRes,
                    onClickNextAction = {
                        logEvent("onboarding3_start_click")
                        onNavigateNextScreen()
                    }
                )
            }
        }
    }
}
