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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.duyvv.basecompose.databinding.LayoutItemNativeFullscreenContentBinding
import com.duyvv.basecompose.utils.NativeAdManager
import com.panda.sdk.ads.api.callback.AdViewCallback

@Composable
fun NativeFullScreenView(
    modifier: Modifier = Modifier.fillMaxWidth(),
    adPlacement: String,
    layoutRes: Int? = null,
    shouldCallRequest: Boolean = true,
    onRemovePage: () -> Unit
) {
    val activity = LocalActivity.current
    val lifecycle = LocalLifecycleOwner.current
    key(adPlacement, shouldCallRequest) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val binding =
                    LayoutItemNativeFullscreenContentBinding.inflate(LayoutInflater.from(context))
                binding.shimmer.visibility = View.VISIBLE
                if (activity != null && shouldCallRequest) {
                    NativeAdManager.show(
                        activity = activity,
                        lifecycleOwner = lifecycle,
                        placement = adPlacement,
                        layoutRes = layoutRes,
                        container = binding.frAds,
                        shimmer = binding.shimmer,
                        listener = object : AdViewCallback {
                            override fun onShowFailed(
                                placement: String,
                                code: Int,
                                message: String
                            ) {

                            }

                            override fun onLoadFailed(
                                placement: String,
                                code: Int,
                                message: String
                            ) {
                                Log.d("TAG", "fullscreen onLoadFailed: ")
                                val shouldRemovePage =
                                    NativeAdManager.shouldRemoveNativeFullScreenPage(
                                        NativeAdManager.NATIVE_OB_FULL_23
                                    )
                                if (shouldRemovePage) {
                                    onRemovePage.invoke()
                                }
                            }
                        }
                    )
                }
                binding.root
            }
        )
    }
}