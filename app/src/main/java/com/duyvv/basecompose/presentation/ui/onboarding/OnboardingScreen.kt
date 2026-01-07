package com.duyvv.basecompose.presentation.ui.onboarding

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.duyvv.basecompose.R
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.data.local.datastore.collectAsState
import com.duyvv.basecompose.presentation.common.composeview.AppText
import com.duyvv.basecompose.presentation.common.composeview.LFONativeView
import com.duyvv.basecompose.presentation.common.composeview.NativeFullScreenView
import com.duyvv.basecompose.presentation.common.logEvent
import com.duyvv.basecompose.presentation.common.noAnimClickable
import com.duyvv.basecompose.presentation.ui.main.MainActivity
import com.duyvv.basecompose.utils.InterAdManager
import com.duyvv.basecompose.utils.NativeAdManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.totalPage })
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val disableBack by AppConfigManager.getInstance().disableBack.collectAsState(null)

    fun onNextScreen() {
        InterAdManager.showInter(
            activity = activity,
            adPlacement = InterAdManager.INTER_ONBOARD,
            lifecycleOwner = lifecycleOwner,
        ) {
            scope.launch {
                AppConfigManager.getInstance().isOnboardingCompleted.set(true)
            }
            activity?.startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
    }

    val onNavigateNextScreen = {
        scope.launch {
            if (pagerState.currentPage < uiState.totalPage - 1) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            } else {
                onNextScreen()
            }
        }
    }

    BackHandler {
        if (disableBack == false) {
            scope.launch { onNavigateNextScreen() }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            viewModel.sendIntent(OnboardingIntent.SelectPage(activity = activity, position = page))
            val hasNativeFull23 = uiState.totalPage == 4
            logEvent(
                when (page) {
                    0 -> "onboarding1_next_view"
                    1 -> "onboarding2_next_view"
                    2 -> if (hasNativeFull23) "onboarding_full_screen23_view" else "onboarding3_next_view"
                    else -> "onboarding3_next_view"
                }
            )
        }
    }

    val isShowNativeOnboarding by
    AppConfigManager.getInstance().isShowNativeOnboard.asFlow.collectAsStateWithLifecycle(null)
    val adConfigState by remember {
        AppConfigManager.getInstance().configNativeOB.asFlow.map { nativeConfig ->
            val isSmall = nativeConfig == "small"
            val layoutRes = NativeAdManager.getLayoutAd(isSmall, false)
            layoutRes to isSmall
        }.flowOn(Dispatchers.IO)
    }.collectAsStateWithLifecycle(initialValue = null)
    val isShowNativeFull23 by AppConfigManager.getInstance().isShowNativeOB23FullScreen.collectAsState(
        null
    )
    LaunchedEffect(isShowNativeFull23) {
        if (isShowNativeFull23 != null) {
            viewModel.sendIntent(OnboardingIntent.UpdateTotalPage(if (isShowNativeFull23 == true) 4 else 3))
        }
    }
    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize(),
        beyondViewportPageCount = uiState.totalPage
    ) { page ->
        Log.d("TAG++", "OnboardingScreen: $page")
        val isSelected = pagerState.currentPage == page
        when (page) {
            0 -> OnboardingPageScreen(
                modifier = Modifier.fillMaxSize(),
                imageResId = R.drawable.img_ob_1,
                titleResId = R.string.ob1_content,
                iconSliderResId = R.drawable.ic_slide_1,
                actionLabelResId = R.string.next,
                shouldCallRequestNative = isSelected && isShowNativeOnboarding == true,
                position = 0,
                adNativeConfig = adConfigState,
                onClickNextAction = {
                    logEvent("onboarding1_next_click")
                    onNavigateNextScreen()
                }
            )

            1 -> OnboardingPageScreen(
                modifier = Modifier.fillMaxSize(),
                imageResId = R.drawable.img_ob_2,
                titleResId = R.string.ob2_content,
                iconSliderResId = R.drawable.ic_slide_2,
                actionLabelResId = R.string.next,
                shouldCallRequestNative = isSelected && isShowNativeOnboarding == true,
                position = 1,
                adNativeConfig = adConfigState,
                onClickNextAction = {
                    logEvent("onboarding2_next_click")
                    onNavigateNextScreen()
                }
            )

            2 -> if (isShowNativeFull23 == true && uiState.shouldShowNativeFull23) {
                OnboardingFullScreen(
                    adPlacement = NativeAdManager.NATIVE_OB_FULL_23,
                    modifier = Modifier,
                    isShowNativeAd = isSelected,
                    onNavigateNextScreen = {
                        onNavigateNextScreen()
                    },
                    onRemovePage = {
                        viewModel.sendIntent(OnboardingIntent.RemoveNativeFull23Page)
                    }
                )
            } else {
                OnboardingPageScreen(
                    modifier = Modifier.fillMaxSize(),
                    imageResId = R.drawable.img_ob_3,
                    titleResId = R.string.ob3_content,
                    iconSliderResId = R.drawable.ic_slide_3,
                    actionLabelResId = R.string.get_started,
                    shouldCallRequestNative = isSelected && isShowNativeOnboarding == true,
                    position = 3,
                    adNativeConfig = adConfigState,
                    onClickNextAction = {
                        logEvent("onboarding3_start_click")
                        onNavigateNextScreen()
                    }
                )
            }

            3 -> if (isShowNativeFull23 == true) {
                OnboardingPageScreen(
                    modifier = Modifier.fillMaxSize(),
                    imageResId = R.drawable.img_ob_3,
                    titleResId = R.string.ob3_content,
                    iconSliderResId = R.drawable.ic_slide_3,
                    actionLabelResId = R.string.get_started,
                    shouldCallRequestNative = isSelected && isShowNativeOnboarding == true,
                    position = 3,
                    adNativeConfig = adConfigState,
                    onClickNextAction = {
                        logEvent("onboarding3_start_click")
                        onNavigateNextScreen()
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingPageScreen(
    modifier: Modifier = Modifier,
    imageResId: Int,
    titleResId: Int,
    descriptionResId: Int? = null,
    iconSliderResId: Int,
    actionLabelResId: Int,
    shouldCallRequestNative: Boolean = false,
    position: Int = 0,
    adNativeConfig: Pair<Int, Boolean>?,
    onClickNextAction: () -> Unit = {}
) {
    Log.d("TAG", "OnboardingPageScreen: $shouldCallRequestNative")
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = imageResId,
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        AppText(
            text = stringResource(titleResId),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconSliderResId),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .aspectRatio(52f / 8f)
            )
            Spacer(Modifier.weight(1f))
            AppText(
                modifier = Modifier.noAnimClickable {
                    onClickNextAction.invoke()
                },
                text = stringResource(actionLabelResId),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                maxLines = 1,
            )
        }
        LFONativeView(
            modifier = Modifier.fillMaxWidth(),
            adPlacement = NativeAdManager.NATIVE_OB,
            layoutRes = adNativeConfig?.first,
            adVisibility = View.INVISIBLE,
            isShowNativeSmall = adNativeConfig?.second == true,
            shouldCallRequest = shouldCallRequestNative && adNativeConfig != null
        )
    }
}

@Composable
fun OnboardingFullScreen(
    modifier: Modifier = Modifier,
    adPlacement: String,
    layoutRes: Int? = null,
    isShowNativeAd: Boolean = false,
    onNavigateNextScreen: () -> Unit,
    onRemovePage: () -> Unit
) {
    LaunchedEffect(isShowNativeAd) {
        val isAutoScroll = AppConfigManager.getInstance().isNativeFullScreenAutoScroll.getValue()
        val autoScrollByTime =
            AppConfigManager.getInstance().nativeFullScreenAutoScrollByTime.getValue()
        if (isAutoScroll && autoScrollByTime > 0 && isShowNativeAd) {
            delay(autoScrollByTime)
            Log.d("TAG+++", "OnboardingFullScreen: onNavigateNextScreen")
            onNavigateNextScreen.invoke()
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF111111)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.img_close_ads),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 25.dp)
                .size(40.dp)
                .align(Alignment.End)
                .noAnimClickable {
                    onNavigateNextScreen.invoke()
                }
                .padding(10.dp)

        )
        NativeFullScreenView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            adPlacement = adPlacement,
            layoutRes = layoutRes,
            shouldCallRequest = isShowNativeAd,
            onRemovePage = onRemovePage
        )
    }
}