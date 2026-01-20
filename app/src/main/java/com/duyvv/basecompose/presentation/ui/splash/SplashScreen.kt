package com.duyvv.basecompose.presentation.ui.splash


import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duyvv.basecompose.R
import com.duyvv.basecompose.presentation.common.TrackingScreen
import com.duyvv.basecompose.presentation.common.composeview.AppText
import com.duyvv.basecompose.presentation.common.composeview.BannerView
import com.duyvv.basecompose.presentation.common.composeview.CustomVectorSeekbar
import com.duyvv.basecompose.presentation.ui.main.MainActivity
import com.duyvv.basecompose.presentation.ui.lfo.LFOActivity
import com.duyvv.basecompose.presentation.ui.onboarding.OnboardingActivity
import com.duyvv.basecompose.presentation.ui.theme.colorGradientPrimary
import com.duyvv.basecompose.utils.BannerAdManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel(),
    navigateNextScreen: suspend () -> Unit = {}
) {
    TrackingScreen("splash_open")
    val context = LocalContext.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var startTime = remember { System.currentTimeMillis() }

    BackHandler {
        if (System.currentTimeMillis() - startTime >= 20_000) {
            viewModel.sendIntent(SplashIntent.SplashAdRequestCompleted)
        }
    }

    LaunchedEffect(Unit) {
        Log.d("TAG", "SplashScreen: $activity")
        activity?.let {
            viewModel.sendIntent(
                SplashIntent.Initialize(
                    activity = activity,
                    owner = lifecycleOwner
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                SplashEffect.NavigateNextScreen -> {
                    activity?.let { viewModel.sendIntent(SplashIntent.PreloadAds(it)) }
                    navigateNextScreen()
                }

                SplashEffect.CallRequestAds -> {
                    startTime = System.currentTimeMillis()
                }
            }
        }.launchIn(this)
    }

    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (logo, appName, anim, loading, loadingDes, banner) = createRefs()
        createVerticalChain(logo, appName, chainStyle = ChainStyle.Packed)
        Image(
            painter = painterResource(R.drawable.icon_splash),
            contentDescription = "logo",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .constrainAs(logo) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(appName.top)
                    verticalBias = 0.2f
                }
                .size(110.dp)
                .padding(bottom = 18.dp)
                .clip(RoundedCornerShape(24.dp))
        )
        AppText(
            text = stringResource(R.string.app_name),
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(appName) {
                start.linkTo(logo.start)
                end.linkTo(logo.end)
                top.linkTo(logo.bottom)
                bottom.linkTo(parent.bottom)
            }
        )
        ProcessSeekbar(
            modifier = Modifier
                .constrainAs(loading) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(loadingDes.top, 14.dp)
                }
                .padding(horizontal = 50.dp),
            duration = 10000L
        )
        AppText(
            text = stringResource(R.string.this_action_may_contain_advertising),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(loadingDes) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, 60.dp)
            }
        )
        if (uiState.isShowBanner) {
            BannerView(
                modifier = Modifier.constrainAs(banner) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                adPlacement = BannerAdManager.BANNER_SPLASH,
                bannerAdHelper = BannerAdManager.bannerAdHelper,
                onAdRequestCompleted = {
                    viewModel.sendIntent(SplashIntent.BannerRequestCompleted)
                }
            )
        }
    }
}

@Composable
fun ProcessSeekbar(
    modifier: Modifier,
    duration: Long
) {
    var internalTime by remember { mutableLongStateOf(0L) }

    val progress by remember(internalTime, duration) {
        derivedStateOf {
            if (duration > 0) {
                (internalTime.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
            } else 0f
        }
    }

    LaunchedEffect(duration) {
        if (duration <= 0) return@LaunchedEffect

        while (true) {
            delay(50L)
            internalTime += 50L
            if (internalTime >= duration) {
                internalTime = 0L
            }
        }
    }

    CustomVectorSeekbar(
        modifier = modifier
            .pointerInput(Unit) {},
        value = progress,
        processGradient = colorGradientPrimary,
        trackHeight = 4.dp,
        trackColor = Color(0xFFDFE7ED),
        onValueChange = {},
        onValueChangeFinished = {},
        thumbSize = 0.dp,
        thumbTouchSize = 0.dp,
        thumbIcon = painterResource(R.drawable.ic_thumb_process)
    )
}