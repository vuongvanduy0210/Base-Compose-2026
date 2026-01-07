package com.duyvv.basecompose.presentation.ui.splash

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.duyvv.basecompose.presentation.base.ComposeActivity
import com.panda.sdk.ads.api.PandaResumeAd

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComposeActivity() {

    companion object {
        const val AD_OPEN_APP = "AD_OPEN_APP"
    }

    @Composable
    override fun Content() {
        SplashScreen(
            modifier = Modifier.fillMaxSize(),
        )
    }

    override fun init() {
        PandaResumeAd.INSTANCE?.setEnabled(false)
    }
}


