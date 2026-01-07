package com.duyvv.basecompose.utils

import com.duyvv.basecompose.BuildConfig
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.panda.sdk.ads.api.PandaBanner
import com.panda.sdk.ads.api.config.BannerCollapsible
import com.panda.sdk.ads.api.config.BannerConfig
import com.panda.sdk.ads.api.config.BannerMode

object BannerAdManager {

    private val _bannerAdHelper by lazy { PandaBanner() }

    val bannerAdHelper
        get() = _bannerAdHelper

    const val BANNER_SPLASH = "Banner_splash"
    const val BANNER_DJ = "Banner_DJ"
    const val BANNER_ALL = "Banner_all"
    const val BANNER_ALL_COLLAPSE = "Banner_all_collapse"

    suspend fun config() {
        _bannerAdHelper.setConfig(
            BannerConfig(
                bannerIds = listOf(BuildConfig.Banner_splash),
                canShowAds = AppConfigManager.getInstance().isShowBannerSplash.getValue(),
                canReload = true,
                placement = BANNER_SPLASH,
                mode = BannerMode.ANCHORED_ADAPTIVE,
                preferFrom = listOf()
            )
        )
        _bannerAdHelper.setConfig(
            BannerConfig(
                bannerIds = listOf(BuildConfig.Banner_DJ),
                canShowAds = AppConfigManager.getInstance().isShowBannerDJ.getValue(),
                canReload = true,
                placement = BANNER_DJ,
                mode = BannerMode.ANCHORED_ADAPTIVE,
                preferFrom = listOf(
                    BANNER_SPLASH,
                    BANNER_ALL
                )
            )
        )
        _bannerAdHelper.setConfig(
            BannerConfig(
                bannerIds = listOf(BuildConfig.Banner_all),
                canShowAds = AppConfigManager.getInstance().isShowBannerAll.getValue(),
                canReload = true,
                placement = BANNER_ALL,
                mode = BannerMode.ANCHORED_ADAPTIVE,
                preferFrom = listOf(
                    BANNER_SPLASH,
                    BANNER_DJ
                )
            )
        )

        _bannerAdHelper.setConfig(
            BannerConfig(
                bannerIds = listOf(BuildConfig.Banner_all),
                canShowAds = AppConfigManager.getInstance().isShowBannerAll.getValue(),
                canReload = true,
                placement = BANNER_ALL_COLLAPSE,
                mode = BannerMode.ANCHORED_ADAPTIVE,
                collapsible = BannerCollapsible.BOTTOM,
                configRateCollapse = AppConfigManager.getInstance().configRateBannerCollapse.getValue(),
                preferFrom = listOf(
                    BANNER_SPLASH,
                    BANNER_DJ
                )
            )
        )
    }
}