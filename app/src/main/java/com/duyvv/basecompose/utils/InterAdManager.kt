package com.duyvv.basecompose.utils

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.duyvv.basecompose.BuildConfig
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.panda.sdk.ads.api.PandaInterstitial
import com.panda.sdk.ads.api.callback.FullscreenAdCallback
import com.panda.sdk.ads.api.config.InterConfig

object InterAdManager {

    private val PandaInterstitial = PandaInterstitial()

    const val INTER_ONBOARD = "Inter_onboard"
    const val INTER_ALL = "Inter_all"

    fun setInterval(interval: Long) {
        PandaInterstitial.setInterval(interval)
    }

    suspend fun config() {
        PandaInterstitial.setConfig(
            InterConfig(
                placement = INTER_ONBOARD,
                interIds = listOf(
                    BuildConfig.Inter_onboard
                ),
                canShowAds = AppConfigManager.getInstance().isShowInterOnboard.getValue()
            )
        )
        PandaInterstitial.setConfig(
            InterConfig(
                placement = INTER_ALL,
                interIds = listOf(
                    BuildConfig.Inter_all
                ),
                canShowAds = AppConfigManager.getInstance().isShowInterAll.getValue()
            )
        )
    }

    fun requestInter(
        activity: Activity?,
        adPlacement: String,
        listener: FullscreenAdCallback = object : FullscreenAdCallback {}
    ) {
        activity?.let {
            PandaInterstitial.request(
                it, adPlacement, listener
            )
        }
    }

    fun showInter(
        activity: Activity?,
        adPlacement: String,
        lifecycleOwner: LifecycleOwner,
        preform: List<String> = listOf(INTER_ONBOARD),
        onAction: () -> Unit
    ) {
        activity?.let {
            PandaInterstitial.show(
                activity = it,
                owner = lifecycleOwner,
                placement = adPlacement,
                listener = object : FullscreenAdCallback {
                    override fun onAdClosed() {
                        onAction()
                        Log.d("TAG===", "onAdClose: $adPlacement")
                    }

                    override fun onAdNextAction() {
                        Log.d("TAG===", "onNextAction: 1111")
                        onAction()
                    }
                },
                preferFrom = preform
            )
        } ?: onAction()
    }
}