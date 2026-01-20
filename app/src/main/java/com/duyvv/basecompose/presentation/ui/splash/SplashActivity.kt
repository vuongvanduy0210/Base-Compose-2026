package com.duyvv.basecompose.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.duyvv.basecompose.presentation.base.ComposeActivity
import com.duyvv.basecompose.presentation.ui.lfo.LFOActivity
import com.duyvv.basecompose.presentation.ui.main.MainActivity
import com.duyvv.basecompose.presentation.ui.onboarding.OnboardingActivity
import com.duyvv.basecompose.utils.isOpenLfo
import com.duyvv.basecompose.utils.isOpenOnboarding
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
            navigateNextScreen = {
                val intentAction = when {
                    isOpenLfo() -> Intent(this, LFOActivity::class.java)
                    isOpenOnboarding() -> Intent(this, OnboardingActivity::class.java)

                    else -> Intent(this, MainActivity::class.java)
                }
                intentAction.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intentAction)
                finish()
            }
        )
    }

    override fun init() {
        PandaResumeAd.INSTANCE?.setEnabled(false)
    }
}


