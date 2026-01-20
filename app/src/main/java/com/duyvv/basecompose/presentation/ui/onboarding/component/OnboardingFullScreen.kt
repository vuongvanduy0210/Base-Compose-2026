package com.duyvv.basecompose.presentation.ui.onboarding.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duyvv.basecompose.R
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.presentation.common.composeview.AppText
import com.duyvv.basecompose.presentation.common.composeview.NativeFullScreenView
import com.duyvv.basecompose.presentation.common.noAnimClickable
import com.duyvv.basecompose.presentation.ui.theme.TextStyleBold
import kotlinx.coroutines.delay

@Composable
fun OnboardingFullScreen(
    modifier: Modifier = Modifier,
    adPlacement: String,
    isShowButtonNext: Boolean,
    layoutRes: Int? = null,
    isShowNativeAd: Boolean = false,
    onNavigateNextScreen: () -> Unit,
    onRemovePage: () -> Unit
) {
    LaunchedEffect(isShowNativeAd) {
        val isAutoScroll = AppConfigManager.getInstance().isNativeFullScreenAutoScroll.getValue()
        val autoScrollByTime =
            AppConfigManager.getInstance().nativeFullScreenAutoScrollByTime.getValue()
        if (isAutoScroll && autoScrollByTime > 0 && isShowNativeAd) {
            delay(autoScrollByTime)
            Log.d("TAG+++", "OnboardingFullScreen: onNavigateNextScreen")
            onNavigateNextScreen.invoke()
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF111111))
            .padding(top = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isShowButtonNext) {
            Image(
                painter = painterResource(R.drawable.img_close_ads),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(10.dp)
                    .size(32.dp)
                    .noAnimClickable {
                        onNavigateNextScreen.invoke()
                    }
            )
        }
        NativeFullScreenView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            adPlacement = adPlacement,
            layoutRes = layoutRes,
            shouldCallRequest = isShowNativeAd,
            onRemovePage = onRemovePage
        )
        if (isShowButtonNext) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_slide_2),
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .aspectRatio(52f / 8f)
                )
                Spacer(Modifier.weight(1f))
                AppText(
                    modifier = Modifier.noAnimClickable {
                        onNavigateNextScreen.invoke()
                    },
                    text = stringResource(R.string.next),
                    style = TextStyleBold.copy(fontSize = 18.sp, color = Color.White),
                    maxLines = 1,
                )
            }
        }
    }
}
