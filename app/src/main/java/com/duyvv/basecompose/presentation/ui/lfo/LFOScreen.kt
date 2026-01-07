package com.duyvv.basecompose.presentation.ui.lfo

import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duyvv.basecompose.R
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.data.local.datastore.collectAsState
import com.duyvv.basecompose.domain.model.Language
import com.duyvv.basecompose.presentation.common.TrackingScreen
import com.duyvv.basecompose.presentation.common.backgroundPrimary
import com.duyvv.basecompose.presentation.common.borderPrimary
import com.duyvv.basecompose.presentation.common.composeview.AppText
import com.duyvv.basecompose.presentation.common.composeview.LFONativeView
import com.duyvv.basecompose.presentation.common.inVisible
import com.duyvv.basecompose.presentation.common.logEvent
import com.duyvv.basecompose.presentation.common.noAnimClickable
import com.duyvv.basecompose.presentation.ui.main.MainActivity
import com.duyvv.basecompose.presentation.ui.onboarding.OnboardingActivity
import com.duyvv.basecompose.utils.NativeAdManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LFOScreen(
    modifier: Modifier = Modifier,
    viewModel: LFOViewModel = koinViewModel(),
    isFromSetting: Boolean = false,
    onClickBack: () -> Unit
) {
    TrackingScreen("language_fo_open")
    var isLogEventSelect by rememberSaveable { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current
    val disableBack by AppConfigManager.getInstance().disableBack.collectAsState(null)
    val scope = rememberCoroutineScope()

    suspend fun navigateNextScreen() {
        val intent = when {
            isFromSetting -> Intent(activity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            viewModel.shouldShowOnboarding() -> Intent(activity, OnboardingActivity::class.java)
            /*isOpenPermission() -> {
                Intent(context, OnboardingActivity::class.java).apply {
                    putExtra(OPEN_PERMISSION, true)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }*/

            else -> Intent(activity, MainActivity::class.java)
        }
        activity?.startActivity(intent)
        activity?.finish()
    }
    BackHandler {
        if (isFromSetting) {
            onClickBack.invoke()
        } else if (disableBack == false) {
            scope.launch { navigateNextScreen() }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                LFOEffect.NavigateNextScreen -> {
                    navigateNextScreen()
                }
            }
        }.launchIn(this)
    }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(LFOIntent.InitializeData)
        if (!isFromSetting && activity != null) {
            viewModel.sendIntent(LFOIntent.RequestNativeLFO2(activity))
        }
    }
    LaunchedEffect(uiState.isShowNativeLFO2, uiState.listLanguage) {
        if (uiState.listLanguage.any { it.isChoose } || uiState.isShowNativeLFO2) {
            activity?.let { viewModel.sendIntent(LFOIntent.RequestNativeOnboarding(it)) }
        }
    }

    val configNativeLFOFlow = AppConfigManager.getInstance().configNativeLFO.asFlow
    val configCTRNativeLFOFlow = AppConfigManager.getInstance().configCTRNativeLFO.asFlow
    val adConfigState by remember {
        combine(configNativeLFOFlow, configCTRNativeLFOFlow) { nativeConfig, ctrConfig ->
            val isSmall = nativeConfig == "small"
            val isSmallCTR = ctrConfig == "small"
            val layoutRes = NativeAdManager.getLayoutAd(isSmall, isSmallCTR)
            layoutRes to isSmall
        }.flowOn(Dispatchers.IO)
    }.collectAsStateWithLifecycle(initialValue = null)

    Box(modifier = Modifier.fillMaxSize()) {
        /*Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.img_bg_common),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )*/
        Column(modifier = modifier.fillMaxSize()) {
            LFOHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 10.dp)
                    .padding(horizontal = 20.dp),
                hasSelectedLanguage = uiState.listLanguage.any { it.isChoose },
                onClickBack = onClickBack,
                isFromSetting = isFromSetting,
                onClickNext = {
                    logEvent("language_fo_save_click")
                    viewModel.sendIntent(LFOIntent.ApplySelectedLanguage(context))
                }
            )
            ListLanguage(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                listLanguage = uiState.listLanguage,
                onClickItem = {
                    if (!isLogEventSelect) {
                        logEvent("language_fo_select")
                        isLogEventSelect = true
                    }
                    viewModel.sendIntent(LFOIntent.SelectLanguage(it, !isFromSetting))
                }
            )
            Log.d("ád", "LFOScreen: 11212")
            LFONativeView(
                modifier = Modifier.fillMaxWidth(),
                adPlacement = if (uiState.isShowNativeLFO2) NativeAdManager.NATIVE_LFO_2 else NativeAdManager.NATIVE_LFO_1,
                layoutRes = adConfigState?.first,
                shouldCallRequest = adConfigState != null,
                isShowNativeSmall = adConfigState?.second == true
            )
        }
    }
}

@Composable
fun ListLanguage(
    modifier: Modifier = Modifier,
    listLanguage: List<Language>,
    onClickItem: (Language) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        items(listLanguage, key = { it.code }) {
            LanguageItem(
                modifier = Modifier.fillMaxWidth(),
                language = it,
                onClickItem = {
                    onClickItem.invoke(it)
                }
            )
        }
    }
}

@Composable
fun LanguageItem(
    modifier: Modifier = Modifier,
    language: Language,
    onClickItem: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .then(
                if (language.isChoose) {
                    Modifier.backgroundPrimary(24.dp)
                } else {
                    Modifier
                        .borderPrimary(radius = 24.dp, width = 1.dp, alpha = 0.5f)
                        .background(color = Color.Black.copy(alpha = 0.4f))
                }
            )
            .noAnimClickable(onClickItem)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(width = 0.5.dp, color = Color(0xFFBEC7D8), shape = CircleShape),
            painter = painterResource(language.idIcon),
            contentDescription = null
        )
        Spacer(Modifier.width(14.dp))
        AppText(
            text = language.internationalName,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color.White
        )
        Image(
            painter = painterResource(
                if (language.isChoose) {
                    R.drawable.ic_language_selected
                } else {
                    R.drawable.ic_language_unselected
                }
            ),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun LFOHeader(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
    isFromSetting: Boolean = false,
    hasSelectedLanguage: Boolean = false,
    onClickNext: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(30.dp)
                .inVisible(isFromSetting)
                .noAnimClickable(onClickBack),
            painter = painterResource(R.drawable.ic_back),
            contentDescription = "Back"
        )
        AppText(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            text = stringResource(R.string.language),
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = Color.White
        )
        Image(
            modifier = Modifier
                .size(30.dp)
                .inVisible(hasSelectedLanguage)
                .noAnimClickable(onClickNext),
            painter = painterResource(R.drawable.ic_unselect_merge),
            contentDescription = "next"
        )
    }
}