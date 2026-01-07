package com.duyvv.basecompose

import android.app.Application
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.di.appModule
import com.duyvv.basecompose.presentation.ui.splash.SplashActivity
import com.google.firebase.FirebaseApp
import com.panda.sdk.ads.api.NetworkProvider
import com.panda.sdk.ads.api.PandaResumeAd
import com.panda.sdk.ads.api.PandaSdk
import com.panda.sdk.ads.api.config.AppsflyerConfig
import com.panda.sdk.ads.api.config.PandaConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppConfigManager.initialize(this)
        initAdConfig()
        startKoin {
            androidContext(this@App)
            modules(appModule())
        }
    }

    private fun initAdConfig() {
        val appsflyerConfig = AppsflyerConfig.Build(BuildConfig.appsflyer_key, true).build()
        val iceBearConfig = PandaConfig.Builder(this, appsflyerConfig, NetworkProvider.ADMOB)
            .addTestDeviceId("ABC123")
            .debug(true)
            .build()
        PandaSdk.setConfig(iceBearConfig)

        PandaResumeAd.init(
            app = this,
            adUnitIds = listOf(
                BuildConfig.AppOpen_resume
            ),
            canShowProvider = { act ->
                act != SplashActivity::class.java
            })
    }
}