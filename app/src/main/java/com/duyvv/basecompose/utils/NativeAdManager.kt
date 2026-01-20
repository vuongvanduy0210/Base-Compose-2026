package com.duyvv.basecompose.utils

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.duyvv.basecompose.BuildConfig
import com.duyvv.basecompose.R
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.panda.sdk.ads.api.PandaNative
import com.panda.sdk.ads.api.callback.AdViewCallback
import com.panda.sdk.ads.api.config.NativeConfig
import java.util.concurrent.atomic.AtomicBoolean

object NativeAdManager {
    private val TAG = NativeAdManager::class.simpleName

    private val nativeAdHelper by lazy { PandaNative() }
    const val NATIVE_LFO_1 = "Native_language"
    const val NATIVE_LFO_2 = "Native_language_S2"
    const val NATIVE_OB = "Native_onboard"
    const val NATIVE_OB_FULL_12 = "Native_OB12_fullscreen"
    const val NATIVE_OB_FULL_23 = "Native_OB23_fullscreen"
    const val NATIVE_ALL = "Native_all"

    suspend fun configAdPreload() {
        // LFO1
        nativeAdHelper.setConfig(
            NativeConfig(
                nativeIds = listOfNotNull(
//                    if (AppConfigManager.getInstance().isShowNativeLanguage2F.getValue()) BuildConfig.Native_language_2F else null,
                    BuildConfig.Native_language,
                ).filter { it.isNotBlank() },
                canShowAds = AppConfigManager.getInstance().isShowNativeLanguage.getValue(),
                canReload = true,
                placement = NATIVE_LFO_1,
                preferFrom = listOf(
                    NATIVE_OB,
                    NATIVE_LFO_2,
                    NATIVE_OB_FULL_12,
                    NATIVE_OB_FULL_23,
                    NATIVE_ALL
                ),
                layoutRes = R.layout.layout_native_big
            )
        )

        // LFO2
        nativeAdHelper.setConfig(
            NativeConfig(
                nativeIds = listOfNotNull(
//                    if (AppConfigManager.getInstance().isShowNativeLanguageS22F.getValue()) BuildConfig.Native_language_S2_2F else null,
                    BuildConfig.Native_language_S2,
                ).filter { it.isNotBlank() },
                canShowAds = AppConfigManager.getInstance().isShowNativeLanguageS2.getValue(),
                canReload = true,
                placement = NATIVE_LFO_2,
                preferFrom = listOf(
                    NATIVE_LFO_1,
                    NATIVE_OB
                ),
                layoutRes = R.layout.layout_native_big
            )
        )

        // OB
        nativeAdHelper.setConfig(
            NativeConfig(
                nativeIds = listOfNotNull(
//                    if (AppConfigManager.getInstance().isShowNativeOnboard2F.getValue()) BuildConfig.Native_onboard_2F else null,
                    BuildConfig.Native_onboard,
                ).filter { it.isNotBlank() },
                canShowAds = AppConfigManager.getInstance().isShowNativeOnboard.getValue(),
                canReload = true,
                placement = NATIVE_OB,
                preferFrom = listOf(
                    NATIVE_LFO_1,
                    NATIVE_LFO_2,
                    NATIVE_OB_FULL_12,
                    NATIVE_OB_FULL_23,
                ),
                layoutRes = R.layout.layout_native_big
            )
        )

        // OB full 12
        nativeAdHelper.setConfig(
            NativeConfig(
                nativeIds = listOfNotNull(
                    BuildConfig.Native_OB12_fullscreen
                ).filter { it.isNotBlank() },
                canShowAds = AppConfigManager.getInstance().isShowNativeOB12FullScreen.getValue(),
                canReload = true,
                placement = NATIVE_OB_FULL_12,
                preferFrom = listOf(
                    NATIVE_OB,
                    NATIVE_LFO_1,
                    NATIVE_LFO_2,
                    NATIVE_OB_FULL_23,
                ),
                layoutRes = R.layout.native_onboarding_full_screen
            )
        )

        // OB full 23
        nativeAdHelper.setConfig(
            NativeConfig(
                nativeIds = listOfNotNull(
                    BuildConfig.Native_OB23_fullscreen
                ).filter { it.isNotBlank() },
                canShowAds = AppConfigManager.getInstance().isShowNativeOB23FullScreen.getValue(),
                canReload = true,
                placement = NATIVE_OB_FULL_23,
                preferFrom = listOf(
                    NATIVE_OB,
                    NATIVE_LFO_1,
                    NATIVE_LFO_2,
                    NATIVE_OB_FULL_12,
                ),
                layoutRes = R.layout.native_onboarding_full_screen
            )
        )
    }

    fun show(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        placement: String,
        layoutRes: Int?,
        container: FrameLayout,
        shimmer: View,
        listener: AdViewCallback
    ) {
        nativeAdHelper.show(
            activity = activity,
            lifecycleOwner = lifecycleOwner,
            placement = placement,
            layoutRes = layoutRes,
            container = container,
            shimmer = shimmer,
            listener = listener
        )
    }

    fun request(
        activity: Activity,
        placement: String,
        listener: AdViewCallback = object : AdViewCallback {}
    ) {
        nativeAdHelper.request(
            activity = activity,
            placement = placement,
            listener = listener
        )
    }

    private val isRequestNativeOnboarding = AtomicBoolean(false)
    fun requestNativeOnboarding(activity: Activity) {
        if (isRequestNativeOnboarding.getAndSet(true)) return
        Log.d(TAG, "requestNativeOnboarding")
        request(activity, NATIVE_OB)
    }

    private val isRequestNativeObFull12 = AtomicBoolean(false)
    fun requestNativeObFull12(activity: Activity) {
        if (isRequestNativeObFull12.getAndSet(true)) return
        request(activity = activity, placement = NATIVE_OB_FULL_12)
    }

    private val isRequestNativeObFull23 = AtomicBoolean(false)
    fun requestNativeObFull23(activity: Activity) {
        if (isRequestNativeObFull23.getAndSet(true)) return
        request(activity = activity, placement = NATIVE_OB_FULL_23)
    }

    fun getLayoutAd(isNativeSmall: Boolean, isCtrSmall: Boolean): Int {
        return if (isNativeSmall) {
            if (isCtrSmall) R.layout.layout_native_medium_ctr_small else R.layout.layout_native_medium
        } else {
            if (isCtrSmall) R.layout.layout_native_big_ctr_small else R.layout.layout_native_big
        }
    }

    fun setReloadNative(adPlacement: String, isReload: Boolean) {
        nativeAdHelper.setReloadable(adPlacement, isReload)
    }

    fun shouldRemoveNativeFullScreenPage(adPlacement: String): Boolean {
        return !nativeAdHelper.contentAdExists(adPlacement)
    }
}