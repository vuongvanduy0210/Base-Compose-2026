package com.duyvv.basecompose.presentation.ui.splash

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.LifecycleOwner
import com.duyvv.basecompose.BuildConfig
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.data.local.datastore.ConfigManager
import com.duyvv.basecompose.presentation.base.ComposeViewModel
import com.duyvv.basecompose.presentation.ui.splash.SplashActivity.Companion.AD_OPEN_APP
import com.duyvv.basecompose.utils.BannerAdManager
import com.duyvv.basecompose.utils.InterAdManager
import com.duyvv.basecompose.utils.NativeAdManager
import com.duyvv.basecompose.utils.RewardAdManager
import com.duyvv.basecompose.utils.isOpenLfo
import com.duyvv.basecompose.utils.isOpenOnboarding
import com.duyvv.basecompose.utils.shouldShowAdOpenAOA
import com.panda.sdk.ads.api.ConsentManager
import com.panda.sdk.ads.api.FullscreenType
import com.panda.sdk.ads.api.PandaOpenAd
import com.panda.sdk.ads.api.PandaResumeAd
import com.panda.sdk.ads.api.callback.FullscreenAdCallback
import com.panda.sdk.ads.api.callback.OnConsentResponse
import com.panda.sdk.ads.api.config.AdOpenConfig
import com.panda.sdk.ads.api.config.AppOpenId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean

@Stable
class SplashViewModel(
    private val context: Application,
    private val configManager: ConfigManager
) : ComposeViewModel<SplashUiState, SplashIntent, SplashEffect>() {

    private var isPreloadAds = AtomicBoolean(false)
    private var isCallNavigateNextScreen = AtomicBoolean(false)
    private var isCallInit = AtomicBoolean(false)

    companion object {
        val TAG = SplashViewModel::class.simpleName
    }

    val pandaOpenAd by lazy { PandaOpenAd() }


    private fun registerObserver(activity: Activity, owner: LifecycleOwner) {
        viewModelSafetyScope.launch {
            val success = withTimeoutOrNull(20_000L) {
                uiState.map { it.interRequestCompleted && it.bannerRequestCompleted }
                    .distinctUntilChanged()
                    .filter { it }
                    .take(1)
                    .collect {
                        Log.d(TAG, "registerObserver: asdfasdfsadfww")
                        if (AppConfigManager.getInstance().configLogicPreload.getValue()) {
                            preloadAds(activity)
                        }
                        delay(1000L)
                        pandaOpenAd.show(activity, owner)
                    }
                true
            }
            if (success == null) {
                if (AppConfigManager.getInstance().configLogicPreload.getValue()) {
                    preloadAds(activity)
                }
                pandaOpenAd.show(activity, owner)
            }
        }
    }

    override fun createInitialState(): SplashUiState {
        return SplashUiState()
    }

    override fun sendIntent(intent: SplashIntent) {
        when (intent) {
            is SplashIntent.Initialize -> {
                if (isCallInit.getAndSet(true)) return
                startInitializationFlow(intent.activity, intent.owner)
            }

            SplashIntent.BannerRequestCompleted -> updateUiState { copy(bannerRequestCompleted = true) }
            is SplashIntent.PreloadAds -> preloadAds(intent.activity)
            SplashIntent.SplashAdRequestCompleted -> updateUiState {
                copy(
                    interRequestCompleted = true,
                    bannerRequestCompleted = true
                )
            }
        }
    }

    suspend fun requestAdOpenApp(activity: Activity, owner: LifecycleOwner) {
        val shouldShowAdOpenAOA = shouldShowAdOpenAOA(
            AppConfigManager.getInstance().configRateAOAInterSplash.getValue()
        )
        Log.d("TAG++++", "requestAdOpenApp: $shouldShowAdOpenAOA")
        val adOpenId = AppOpenId(
            fullscreenType = if (shouldShowAdOpenAOA) FullscreenType.APP_OPEN else FullscreenType.INTER,
            adIds = buildList {
                if (shouldShowAdOpenAOA) {
                    /*if (AppConfigManager.getInstance().isShowAdOpenApp2F.getValue()) {
                        add(BuildConfig.Ad_open_app_2F)
                    }*/
                    add(BuildConfig.Ad_open_app)
                } else {
                    /*if (AppConfigManager.getInstance().isShowInterSplash2F.getValue()) {
                        add(BuildConfig.Inter_splash_2F)
                    }*/
                    add(BuildConfig.Inter_splash)
                }
            }.filter { it.trim().isNotBlank() }
        )
        val cfg = AdOpenConfig(
            appOpenIds = adOpenId,
            timeOutMs = 20_000L,
            timeDelayMs = 5_000,
            showReady = false,
            canShowAds = AppConfigManager.getInstance().isShowAdSplash.getValue(),
            placement = AD_OPEN_APP,
            object : FullscreenAdCallback {
                override fun onAdClosed() {
                    super.onAdClosed()
                    callNavigateNextScreen()
                }

                override fun onAdNextAction() {
                    callNavigateNextScreen()
                }

                override fun onAdImpression(placement: String) {

                }

                override fun onLoadFailed(
                    placement: String,
                    code: Int,
                    message: String
                ) {
                    updateUiState { copy(interRequestCompleted = true) }
                }

                override fun onLoaded(placement: String) {
                    updateUiState { copy(interRequestCompleted = true) }
                }

                override fun onShowFailed(
                    placement: String,
                    code: Int,
                    message: String
                ) {
                    updateUiState { copy(interRequestCompleted = true) }
                }
            }
        ).validated()
        pandaOpenAd.request(activity = activity, owner = owner, cfgRaw = cfg)
    }

    private fun callNavigateNextScreen() {
        if (isCallNavigateNextScreen.getAndSet(true)) return
        sendEffect { SplashEffect.NavigateNextScreen }
    }

    private fun startInitializationFlow(activity: Activity, owner: LifecycleOwner) {
        Log.d(TAG, "startInitializationFlow")
        viewModelSafetyScope.launch { // Chạy background để tránh block UI
            val isAvailable = isInternetAvailable(context)
            if (isAvailable) {
                Log.d(TAG, "request ump + remote config")
                val consentDeferred = async(Dispatchers.IO) {
                    runCatching { handleConsentSuspend(activity) }
                        .onFailure { Log.e(TAG, "Consent error", it) }
                        .getOrDefault(true)
                }

                val remoteConfigJob = launch(Dispatchers.IO) {
                    try {
                        loadRemoteConfigSuspend()
                    } catch (e: Exception) {
                        Log.e(TAG, "RemoteConfig error", e)
                    }
                }
                val configFinishedInTime = withTimeoutOrNull(4_000L) {
                    remoteConfigJob.join()
                    true
                } ?: false

                val consentOk = consentDeferred.await()

                Log.d(
                    TAG,
                    "Config success=$configFinishedInTime, consentOk=$consentOk, proceed to ads"
                )
                requestAds(
                    isNetworkValid = true,
                    activity = activity,
                    owner = owner
                )
            } else {
                Log.w(TAG, "No network — skip config & consent")
                requestAds(
                    isNetworkValid = false,
                    activity = activity,
                    owner = owner
                )
            }
        }
    }

    private suspend fun requestAds(
        isNetworkValid: Boolean = true,
        activity: Activity,
        owner: LifecycleOwner
    ) = withContext(Dispatchers.Main) {
        Log.d(TAG, "requestAds: $isNetworkValid")
        sendEffect { SplashEffect.CallRequestAds }
        registerObserver(activity, owner)
        BannerAdManager.config()

        PandaResumeAd.INSTANCE?.setEnabled(false)
        NativeAdManager.configAdPreload()
        RewardAdManager.config()
        InterAdManager.config()
        InterAdManager.setInterval(1L)

        val config = AppConfigManager.getInstance()
        if (config.isShowAdSplash.getValue() && isNetworkValid) {
            requestAdOpenApp(activity, owner)
        } else {
            requestAdOpenApp(activity, owner)
            updateUiState { copy(interRequestCompleted = true) }
        }

        if (config.isShowBannerSplash.getValue() && isNetworkValid) {
            updateUiState { copy(isShowBanner = true) }
        } else {
            updateUiState { copy(bannerRequestCompleted = true) }
        }
    }

    private fun preloadAds(activity: Activity) {
        if (isPreloadAds.getAndSet(true)) return
        Log.e(TAG, "preload: Ads")
        viewModelSafetyScope.launch {
            if (isOpenLfo()) {
                NativeAdManager.request(
                    activity = activity,
                    placement = NativeAdManager.NATIVE_LFO_1
                )
            } else if (isOpenOnboarding()) {
                NativeAdManager.requestNativeOnboarding(activity)
            }
        }
    }

    private suspend fun handleConsentSuspend(activity: Activity): Boolean {
        return suspendCancellableCoroutine { cont ->
            val resumed = AtomicBoolean(false)
            runCatching {
                ConsentManager.getInstance(activity)
                    .initReleaseConsent(object : OnConsentResponse {
                        override fun onResponse(errorMessage: String?) {
                            if (resumed.compareAndSet(false, true) && cont.isActive) {
                                cont.resume(true) { cause, _, _ -> }
                            }
                            if (errorMessage != null) {
                                Log.e(TAG, "Consent error: $errorMessage")
                            } else {
                                Log.d(TAG, "User consent obtained")
                            }
                        }

                        override fun onPolicyRequired(isRequired: Boolean) {
                        }
                    })
            }.onFailure { e ->
                Log.e(TAG, "handleConsentSuspend error", e)
                if (resumed.compareAndSet(false, true) && cont.isActive) {
                    cont.resume(true) { cause, _, _ -> }
                }
            }
            cont.invokeOnCancellation {
                if (resumed.compareAndSet(false, true)) {
                    cont.resume(true) { cause, _, _ -> }
                }
            }
        }
    }

    private suspend fun loadRemoteConfigSuspend() {
        try {
            configManager.loadRemoteConfig()
        } catch (e: Exception) {
            Log.e("TAG", "loadRemoteConfigSuspend error", e)
        }
    }

    suspend fun isInternetAvailable(context: Context, timeoutMs: Int = 1500): Boolean {
        if (!hasNetworkConnection(context)) return false
        return hasUsableInternet(timeoutMs)
    }

    fun hasNetworkConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        val network = cm.activeNetwork ?: return false
        val nc = cm.getNetworkCapabilities(network) ?: return false
        return nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                || nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun hasUsableInternet(timeoutMs: Int = 2000): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://clients3.google.com/generate_204")
                (url.openConnection() as? HttpURLConnection)?.run {
                    connectTimeout = timeoutMs
                    readTimeout = timeoutMs
                    requestMethod = "GET"
                    connect()
                    responseCode == 204
                } ?: false
            } catch (e: Exception) {
                Log.e("TAG", "hasUsableInternet: ${e.message}")
                false
            }
        }
    }
}
