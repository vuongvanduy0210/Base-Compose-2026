package com.duyvv.basecompose.presentation.ui.lfo

import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.presentation.base.ComposeViewModel
import com.duyvv.basecompose.utils.LanguageUtils
import com.duyvv.basecompose.utils.NativeAdManager
import kotlinx.coroutines.launch

class LFOViewModel : ComposeViewModel<LFOUiState, LFOIntent, LFOEffect>() {
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