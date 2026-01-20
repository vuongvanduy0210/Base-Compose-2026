package com.duyvv.basecompose.data.local.datastore

import android.util.Log
import com.duyvv.basecompose.R
import com.duyvv.basecompose.utils.KeyRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.text.set

class ConfigManager {
    suspend fun loadRemoteConfig(): Boolean = withContext(Dispatchers.IO) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        try {
            val fetchSuccessful = remoteConfig.fetchAndActivate().await()
            applyRemoteConfig(remoteConfig)
            Log.e("ConfigManager", "loadRemoteConfig: Success (Updated: $fetchSuccessful)")
            true
        } catch (e: Exception) {
            Log.e("ConfigManager", "Remote config fetch failed: ${e.message}", e)
            true
        }
    }

    private suspend fun applyRemoteConfig(remoteConfig: FirebaseRemoteConfig) {
        with(AppConfigManager.Companion.getInstance()) {
            isShowUmp.set(remoteConfig.getBoolean(KeyRemoteConfig.ENABLE_UMP))
            isShowAdSplash.set(remoteConfig.getBoolean(KeyRemoteConfig.CONFIG_AD_SPLASH))
//            isShowInterSplash2F.set(remoteConfig.getBoolean(KeyRemoteConfig.INTER_SPLASH_2F))
//            isShowAdOpenApp2F.set(remoteConfig.getBoolean(KeyRemoteConfig.AD_OPEN_APP_2F))
            isShowAppOpenResume.set(remoteConfig.getBoolean(KeyRemoteConfig.APP_OPEN_RESUME))
            isShowBannerSplash.set(remoteConfig.getBoolean(KeyRemoteConfig.BANNER_SPLASH))
            isShowNativeLanguage.set(remoteConfig.getBoolean(KeyRemoteConfig.NATIVE_LANGUAGE))
//            isShowNativeLanguage2F.set(remoteConfig.getBoolean(KeyRemoteConfig.NATIVE_LANGUAGE_2F))
            isShowNativeLanguageS2.set(remoteConfig.getBoolean(KeyRemoteConfig.NATIVE_LANGUAGE_S2))
//            isShowNativeLanguageS22F.set(remoteConfig.getBoolean(KeyRemoteConfig.NATIVE_LANGUAGE_S2_2F))
            isShowNativeOnboard.set(remoteConfig.getBoolean(KeyRemoteConfig.NATIVE_ONBOARDING))
//            isShowNativeOnboard2F.set(remoteConfig.getBoolean(KeyRemoteConfig.NATIVE_OB_2F))
            isShowNativeOB12FullScreen.set(remoteConfig.getBoolean(KeyRemoteConfig.NATIVE_FULLSCREEN_OB12))
            isShowNativeOB23FullScreen.set(remoteConfig.getBoolean(KeyRemoteConfig.NATIVE_FULLSCREEN_OB23))
            isShowInterOnboard.set(remoteConfig.getBoolean(KeyRemoteConfig.INTER_ONBOARD))
            isShowBannerAll.set(remoteConfig.getBoolean(KeyRemoteConfig.BANNER_ALL))
            isShowInterAll.set(remoteConfig.getBoolean(KeyRemoteConfig.INTER_ALL))
            configRateAOAInterSplash.set(
                remoteConfig.getLong(KeyRemoteConfig.CONFIG_RATE_AOA_INTER_SPLASH).toInt()
            )
            intervalBetweenInterstitial.set(remoteConfig.getLong(KeyRemoteConfig.INTERVAL_BETWEEN_INTERSTITIAL))
            isNativeFullScreenAutoScroll.set(remoteConfig.getBoolean(KeyRemoteConfig.NATIVE_FULL_SCREEN_AUTO_SCROLL))
            nativeFullScreenAutoScrollByTime.set(
                remoteConfig.getLong(KeyRemoteConfig.NATIVE_FULL_SCREEN_AUTO_SCROLL_BY_TIME)
            )
            languageReopen.set(remoteConfig.getBoolean(KeyRemoteConfig.LANGUAGE_REOPEN))
            onboardReopen.set(remoteConfig.getBoolean(KeyRemoteConfig.OB_REOPEN))
            disableBack.set(remoteConfig.getBoolean(KeyRemoteConfig.DISABLE_BACK))
            configLogicPreload.set(remoteConfig.getBoolean(KeyRemoteConfig.CONFIG_LOGIC_PRELOAD))
            configNativeLFO.set(remoteConfig.getString(KeyRemoteConfig.CONFIG_NATIVE_LFO))
            configNativeOB.set(remoteConfig.getString(KeyRemoteConfig.CONFIG_NATIVE_OB))
            Log.d("SplashViewModel", "applyRemoteConfig complete")
        }
    }
}