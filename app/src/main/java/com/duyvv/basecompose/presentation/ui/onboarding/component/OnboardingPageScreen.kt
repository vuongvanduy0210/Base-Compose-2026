package com.duyvv.basecompose.presentation.ui.onboarding.component

import android.util.Log
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.duyvv.basecompose.presentation.common.composeview.AppText
import com.duyvv.basecompose.presentation.common.composeview.NativeView
import com.duyvv.basecompose.presentation.common.noAnimClickable
import com.duyvv.basecompose.presentation.ui.theme.TextStyleBold
import com.duyvv.basecompose.presentation.ui.theme.TextStyleSemiBold
import com.duyvv.basecompose.utils.NativeAdManager
import com.panda.sdk.ads.api.config.AdOptionVisibility

@Composable
fun OnboardingPageScreen(
    modifier: Modifier = Modifier,
    layoutNativeResId: Int? = null,
    isShowNativeBig: Boolean? = null,
    imageResId: Int,
    titleResId: Int,
    descriptionResId: Int? = null,
    iconSliderResId: Int,
    actionLabelResId: Int,
    shouldCallRequestNative: Boolean = false,
    position: Int = 0,
    onClickNextAction: () -> Unit = {}
) {
    Log.d("TAG", "OnboardingPageScreen: $position")
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = imageResId,
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentScale = ContentScale.FillHeight
        )
        Spacer(Modifier.height(20.dp))
        AppText(
            text = stringResource(titleResId),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            style = TextStyleSemiBold.copy(fontSize = 20.sp)
        )
        if (isShowNativeBig != null) {
            Spacer(Modifier.height(12.dp))
            NativeView(
                modifier = Modifier.fillMaxWidth(),
                adPlacement = NativeAdManager.NATIVE_OB,
                layoutRes = layoutNativeResId,
                adVisibility = AdOptionVisibility.INVISIBLE,
                isShowNativeSmall = !isShowNativeBig,
                shouldCallRequest = shouldCallRequestNative
            )
            Spacer(Modifier.height(12.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconSliderResId),
                contentDescription = null,
                modifier = Modifier
                    .size(52.dp)
                    .aspectRatio(52f / 8f)
            )
            Spacer(Modifier.weight(1f))
            AppText(
                modifier = Modifier.noAnimClickable {
                    onClickNextAction.invoke()
                },
                text = stringResource(actionLabelResId),
                style = TextStyleBold.copy(fontSize = 18.sp),
                maxLines = 1,
            )
        }
    }
}
