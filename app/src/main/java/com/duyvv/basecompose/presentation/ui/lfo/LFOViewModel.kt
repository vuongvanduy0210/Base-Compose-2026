package com.duyvv.basecompose.presentation.ui.lfo

import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.presentation.base.ComposeViewModel
import com.duyvv.basecompose.utils.LanguageUtils
import com.duyvv.basecompose.utils.NativeAdManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class LFOViewModel : ComposeViewModel<LFOUiState, LFOIntent, LFOEffect>() {

    init {
        combine(
            AppConfigManager.getInstance().configNativeLFO.asFlow,
            AppConfigManager.getInstance().configCTRNativeLFO.asFlow
        ) { nativeConfig, ctrConfig ->
            val isNativeSmall = nativeConfig == "small"
            val isCtrSmall = ctrConfig == "small"
            val layoutResId = NativeAdManager.getLayoutAd(
                isNativeBig = !isNativeSmall,
                isCtrBig = !isCtrSmall
            )
            updateUiState { copy(nativeLayoutRes = layoutResId, isShowNativeBig = !isNativeSmall) }
        }.launchIn(viewModelSafetyScope)

        AppConfigManager.getInstance().disableBack.asFlow.mapLatest {
            updateUiState {
                copy(disableBack = it)
            }
        }.launchIn(viewModelSafetyScope)
    }

    override fun createInitialState(): LFOUiState {
        return LFOUiState()
    }

    override fun sendIntent(intent: LFOIntent) {
        when (intent) {
            LFOIntent.InitializeData -> {
                initData()
            }

            is LFOIntent.SelectLanguage -> {
                updateUiState {
                    copy(
                        listLanguage = listLanguage.map {
                            it.copy(isChoose = intent.language.code == it.code)
                        },
                        isShowNativeLFO2 = intent.isShowNativeLFO2
                    )
                }
            }

            is LFOIntent.RequestNativeLFO2 -> {
                NativeAdManager.request(intent.activity, NativeAdManager.NATIVE_LFO_2)
            }

            is LFOIntent.RequestNativeOnboarding -> {
                NativeAdManager.requestNativeOnboarding(intent.activity)
            }

            is LFOIntent.ApplySelectedLanguage -> {
                val selected = uiState.value.listLanguage.firstOrNull { it.isChoose } ?: return
                viewModelSafetyScope.launch {
                    LanguageUtils.changeLang(selected.code, intent.context)
                    sendEffect { LFOEffect.NavigateNextScreen }
                }
            }
        }
    }

    private fun initData() {
        viewModelSafetyScope.launch {
            val listLanguage = LanguageUtils.getListLanguage()
            updateUiState { copy(listLanguage = listLanguage) }
        }
    }

    suspend fun shouldShowOnboarding(): Boolean {
        val config = AppConfigManager.getInstance()
        return !config.isOnboardingCompleted.getValue() || config.onboardReopen.getValue()
    }
}