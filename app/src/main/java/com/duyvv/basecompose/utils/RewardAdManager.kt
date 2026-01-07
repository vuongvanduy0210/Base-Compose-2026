package com.duyvv.basecompose.utils

import android.app.Activity
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.duyvv.basecompose.BuildConfig
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.panda.sdk.ads.api.PandaReward
import com.panda.sdk.ads.api.callback.FullscreenAdCallback
import com.panda.sdk.ads.api.config.RewardedConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.cancellation.CancellationException

object RewardAdManager {

    private val _rewardAdManager by lazy { PandaReward() }
    private const val REQUEST_TIMEOUT_MS = 10_000L

    val rewardAdHelper
        get() = _rewardAdManager

    const val REWARD_ALL = "Reward_all"

    suspend fun config() {
        _rewardAdManager.setConfig(
            RewardedConfig(
                rewardedIds = listOf(BuildConfig.Reward_all),
                placement = REWARD_ALL,
                canShowAds = AppConfigManager.getInstance().isShowRewardAll.getValue()
            )
        )
    }

    suspend fun showRewardOnce(
        activity: Activity?,
        lifecycleOwner: LifecycleOwner,
        adPlacement: String,
        onShowLoading: (Boolean) -> Unit
    ): Boolean = withContext(Dispatchers.Main) {
        if (activity == null || activity.isFinishing || activity.isDestroyed) return@withContext false
        onShowLoading.invoke(true)

        return@withContext try {
            val loaded = withTimeoutOrNull(REQUEST_TIMEOUT_MS) {
                suspendCancellableCoroutine<Boolean> { cont ->
                    rewardAdHelper.request(activity, adPlacement, object : FullscreenAdCallback {
                        override fun onLoaded(placement: String) {
                            Log.d("RewardAdManager", "Ad loaded successfully for $adPlacement")
                            cont.resume(true) { cause, _, _ -> }
                        }

                        override fun onLoadFailed(
                            placement: String,
                            code: Int,
                            message: String
                        ) {
                            Log.e("RewardAdManager", "onAdFailedToLoad: $message")
                            cont.resume(false) { cause, _, _ -> }
                        }
                    })

                    cont.invokeOnCancellation {
                        Log.w("RewardAdsManager", "Ad request cancelled for $adPlacement")
                    }
                }
            } ?: return@withContext false

            if (activity.isFinishing || activity.isDestroyed
                || !lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) || !loaded
            ) return@withContext false

            val shown = suspendCancellableCoroutine { cont ->
                var hasStartedShowing = false
                rewardAdHelper.show(
                    activity = activity,
                    placement = adPlacement,
                    listener = object : FullscreenAdCallback {
                        override fun onAdImpression(placement: String) {
                            super.onAdImpression(placement)
                            hasStartedShowing = true
                            Log.d("RewardAdsManager", "onAdImpression")
                        }

                        override fun onUserEarnedReward() {
                            super.onUserEarnedReward()
                            Log.d("RewardAdsManager", "onUserEarnedReward")
                        }

                        override fun onAdNextAction() {
                            super.onAdNextAction()
                            Log.d("RewardAdsManager", "onAdNextAction")
                        }


                        /*override fun onRewardShow() {
                            super.onRewardShow()
                            hasStartedShowing = true
                            Log.d("RewardAdsManager", "onRewardShow")
                        }*/

                        override fun onAdClosed() {
                            super.onAdClosed()
                            if (hasStartedShowing) {
                                Log.d("RewardAdsManager", "Ad closed for $adPlacement")
                                if (cont.isActive) cont.resume(true) { cause, _, _ -> }
                            } else {
                                Log.d("RewardAdsManager", "Ad NextAction for $adPlacement")
                                if (cont.isActive) cont.resume(false) { cause, _, _ -> }
                            }
                        }

                        override fun onShowFailed(
                            placement: String,
                            code: Int,
                            message: String
                        ) {
                            super.onShowFailed(placement, code, message)
                            if (!hasStartedShowing) {
                                Log.e("RewardAdsManager", "onAdFailedToShow: ${message}")
                                if (cont.isActive) cont.resume(false) { cause, _, _ -> }
                            } else {
                                Log.w(
                                    "RewardAdsManager",
                                    "Ignore onAdFailedToShow because ad was already shown"
                                )
                            }
                        }
                    }
                )

                cont.invokeOnCancellation {
                    if (!hasStartedShowing) {
                        Log.w("RewardAdsManager", "Ad show cancelled for $adPlacement")
                    } else {
                        Log.w(
                            "RewardAdsManager",
                            "Cancellation ignored because ad was already showing"
                        )
                    }
                }
            }
            shown
        } catch (e: CancellationException) {
            Log.w("RewardAdsManager", "Coroutine cancelled for $adPlacement")
            false
        } finally {
            if (!activity.isFinishing && !activity.isDestroyed) {
                onShowLoading.invoke(false)
            }
        }
    }
}