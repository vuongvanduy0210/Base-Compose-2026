package com.duyvv.basecompose.presentation.common.composeview

import android.view.LayoutInflater
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.duyvv.basecompose.databinding.LayoutItemBannerContentBinding
import com.panda.sdk.ads.api.PandaBanner
import com.panda.sdk.ads.api.callback.AdViewCallback

@Composable
fun BannerView(
    modifier: Modifier = Modifier,
    adPlacement: String,
    bannerAdHelper: PandaBanner,
    onAdRequestCompleted: () -> Unit = {},
) {
    val activity = LocalActivity.current
    val lifecycle = LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val binding = LayoutItemBannerContentBinding.inflate(LayoutInflater.from(context))
            activity?.let {
                bannerAdHelper.show(
                    activity = it,
                    lifecycleOwner = lifecycle,
                    placement = adPlacement,
                    container = binding.frAds,
                    shimmer = binding.shimmerBanner,
                    listener = object : AdViewCallback {
                        override fun onAdLoaded() {
                            onAdRequestCompleted.invoke()
                        }

                        override fun onLoadFailed(
                            placement: String,
                            code: Int,
                            message: String
                        ) {
                            onAdRequestCompleted.invoke()
                        }

                        override fun onShowFailed(
                            placement: String,
                            code: Int,
                            message: String
                        ) {
                            onAdRequestCompleted.invoke()
                        }
                    }
                )
            }
            binding.root
        }
    )
}