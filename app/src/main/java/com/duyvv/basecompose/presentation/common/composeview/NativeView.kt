package com.duyvv.basecompose.presentation.common.composeview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.duyvv.basecompose.databinding.LayoutItemNativeContentBinding
import com.duyvv.basecompose.utils.NativeAdManager
import com.panda.sdk.ads.api.callback.AdViewCallback

@Composable
fun NativeView(
    modifier: Modifier = Modifier,
    adPlacement: String,
    layoutRes: Int? = null,
    shouldCallRequest: Boolean = true,
    adVisibility: Int = View.GONE,
    isShowNativeSmall: Boolean = false,
    onShowBtnCloseAds: (Boolean) -> Unit = {},
    onAdLoaded: () -> Unit = {}
) {
    Log.d("TAG+++", "NativeView: ")
    val activity = LocalActivity.current
    val lifecycle = LocalLifecycleOwner.current
    key(adPlacement, shouldCallRequest) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                Log.d("TAG", "NativeView: ádfasdfwwwwwwwwwww")
                val binding = LayoutItemNativeContentBinding.inflate(
                    LayoutInflater.from(context)
                )
                binding.shimmerBig.isVisible = !isShowNativeSmall
                binding.shimmerMedium.isVisible = isShowNativeSmall
                if (activity == null) {
                    Log.w("NativeView", "Activity or binding is null, skipping ads operation")
                    return@AndroidView binding.root
                }
                val shimmer = if (isShowNativeSmall) {
                    binding.shimmerMedium
                } else {
                    binding.shimmerBig
                }
                if (shouldCallRequest) {
                    Log.w("NativeView", "call show native ads $adPlacement")
                    NativeAdManager.show(
                        activity = activity,
                        lifecycleOwner = lifecycle,
                        placement = adPlacement,
                        layoutRes = layoutRes,
                        container = binding.frAds,
                        shimmer = shimmer,
                        listener = object : AdViewCallback {
                            override fun onShowFailed(
                                placement: String,
                                code: Int,
                                message: String
                            ) {
                                Log.d(
                                    "NativeView",
                                    "onShowFailed: placement=$placement, code=$code, message=$message"
                                )
                                onShowBtnCloseAds.invoke(false)
                            }

                            override fun onLoadFailed(
                                placement: String,
                                code: Int,
                                message: String
                            ) {
                                Log.d(
                                    "NativeView",
                                    "onLoadFailed: placement=$placement, code=$code, message=$message"
                                )
                                shimmer.visibility = adVisibility
                                onShowBtnCloseAds.invoke(false)
                            }

                            override fun onAdLoaded() {
                                Log.d("NativeView", "onAdLoaded: placement=$adPlacement")
                                onAdLoaded.invoke()
                            }

                            override fun onAdImpression() {
                                super.onAdImpression()
                                onShowBtnCloseAds.invoke(true)
                            }
                        }
                    )
                } else {
                    shimmer.visibility = adVisibility
                }
                binding.root
            },
            update = { _ ->
                Log.d("TAG+++", "NativeView: update")
            }
        )
    }
}