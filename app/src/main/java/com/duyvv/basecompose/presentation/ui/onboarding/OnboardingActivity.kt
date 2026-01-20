package com.duyvv.basecompose.presentation.ui.onboarding

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.presentation.base.ComposeActivity
import com.duyvv.basecompose.presentation.ui.main.MainActivity
import com.duyvv.basecompose.utils.InterAdManager
import kotlinx.coroutines.launch

class OnboardingActivity : ComposeActivity() {

    @Composable
    override fun Content() {
        OnboardingScreen(
            onNextScreen = {
                InterAdManager.showInter(
                    activity = this,
                    adPlacement = InterAdManager.INTER_ONBOARD,
                    lifecycleOwner = this,
                ) {
                    lifecycleScope.launch {
                        AppConfigManager.getInstance().isOnboardingCompleted.set(true)
                        val intent = Intent(
                            this@OnboardingActivity,
                            MainActivity::class.java
                        )
                        startActivity(intent)
                        finishAffinity()
                    }
                }
            }
        )
    }
}
