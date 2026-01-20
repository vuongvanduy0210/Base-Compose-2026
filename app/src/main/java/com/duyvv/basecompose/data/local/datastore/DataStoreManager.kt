package com.duyvv.basecompose.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.duyvv.basecompose.utils.KeyLocalConfig
import com.duyvv.basecompose.utils.KeyRemoteConfig

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "money_tracker_datastore")

class AppConfigManager private constructor(
    context: Context
) : BasePreferenceManager(context.dataStore) {

    companion object {
        @Volatile
        private var INSTANCE: AppConfigManager? = null

        fun initialize(context: Context): AppConfigManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppConfigManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        fun getInstance(): AppConfigManager {
            return INSTANCE
                ?: throw IllegalStateException("AppConfigManager is not initialized. Call initialize() first.")
        }
    }

    val isShowUmp = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.ENABLE_UMP),
        default = true
    )
    val isShowAdSplash = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.CONFIG_AD_SPLASH),
        default = true
    )

    /*    val isShowInterSplash2F = booleanPref(
            key = booleanPreferencesKey(KeyRemoteConfig.INTER_SPLASH_2F),
            default = true
        )
        val isShowAdOpenApp2F = booleanPref(
            key = booleanPreferencesKey(KeyRemoteConfig.AD_OPEN_APP_2F),
            default = true
        )*/
    val isShowAppOpenResume = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.APP_OPEN_RESUME),
        default = true
    )
    val isShowBannerSplash = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.BANNER_SPLASH),
        default = true
    )
    val isShowNativeLanguage = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_LANGUAGE),
        default = true
    )

    /* val isShowNativeLanguage2F = booleanPref(
         key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_LANGUAGE_2F),
         default = true
     )*/
    val isShowNativeLanguageS2 = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_LANGUAGE_S2),
        default = true
    )

    /*    val isShowNativeLanguageS22F = booleanPref(
            key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_LANGUAGE_S2_2F),
            default = true
        )*/
    val isShowNativeOnboard = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_ONBOARDING),
        default = true
    )

    /*    val isShowNativeOnboard2F = booleanPref(
            key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_OB_2F),
            default = true
        )*/
    val isShowNativeOB12FullScreen = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_FULLSCREEN_OB12),
        default = true
    )

    val isShowNativeOB23FullScreen = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_FULLSCREEN_OB23),
        default = true
    )
    val isShowInterOnboard = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.INTER_ONBOARD),
        default = true
    )
    val isShowInterAll = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.INTER_ALL),
        default = true
    )
    val isShowNativeHome = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_HOME),
        default = true
    )
    val isShowNativeSound = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_SOUND),
        default = true
    )
    val isShowNativeGuide = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_GUIDE),
        default = true
    )
    val isShowRewardAll = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.REWARD_ALL),
        default = true
    )
    val isShowBannerDJ = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.BANNER_DJ),
        default = true
    )
    val isShowBannerAll = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.BANNER_ALL),
        default = true
    )

    val configRateAOAInterSplash = intPref(
        key = intPreferencesKey(KeyRemoteConfig.CONFIG_RATE_AOA_INTER_SPLASH),
        default = 10
    )
    val intervalBetweenInterstitial = longPref(
        key = longPreferencesKey(KeyRemoteConfig.INTERVAL_BETWEEN_INTERSTITIAL),
        default = 20_000L
    )

    val isNativeFullScreenAutoScroll = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.NATIVE_FULL_SCREEN_AUTO_SCROLL),
        default = true
    )

    val nativeFullScreenAutoScrollByTime = longPref(
        key = longPreferencesKey(KeyRemoteConfig.NATIVE_FULL_SCREEN_AUTO_SCROLL_BY_TIME),
        default = 6_000L
    )

    val configRateBannerCollapse = intPref(
        key = intPreferencesKey(KeyRemoteConfig.CONFIG_RATE_BANNER_COLLAPSE),
        default = 100
    )

    val languageReopen = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.LANGUAGE_REOPEN),
        default = true
    )
    val onboardReopen = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.OB_REOPEN),
        default = true
    )
    val disableBack = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.DISABLE_BACK),
        default = true
    )
    val configLogicPreload = booleanPref(
        key = booleanPreferencesKey(KeyRemoteConfig.CONFIG_LOGIC_PRELOAD),
        default = true
    )

    val configNativeLFO = stringPref(
        key = stringPreferencesKey(KeyRemoteConfig.CONFIG_NATIVE_LFO),
        default = "medium"
    )

    val configNativeOB = stringPref(
        key = stringPreferencesKey(KeyRemoteConfig.CONFIG_NATIVE_OB),
        default = "medium"
    )

    val configCTRNativeLFO = stringPref(
        key = stringPreferencesKey(KeyRemoteConfig.CONFIG_CTA_NATIVE_LFO_BUTTON),
        default = "small"
    )

    // local
    val languageCode = stringPref(
        key = stringPreferencesKey(KeyLocalConfig.LANGUAGE_CODE),
        default = ""
    )
    val isOnboardingCompleted = booleanPref(
        key = booleanPreferencesKey(KeyLocalConfig.ONBOARDING_COMPLETE),
        default = false
    )
}