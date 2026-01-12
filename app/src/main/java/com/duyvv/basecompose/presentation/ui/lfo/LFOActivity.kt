package com.duyvv.basecompose.presentation.ui.lfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.presentation.base.ComposeActivity
import com.duyvv.basecompose.presentation.ui.main.MainActivity
import com.duyvv.basecompose.presentation.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.launch

class LFOActivity : ComposeActivity() {

    @Composable
    override fun Content() {
        val isFromSetting = intent.getBooleanExtra("open_from_setting", false)
        LFOScreen(
            isFromSetting = isFromSetting,
            onClickBack = {
                finish()
            },
            navigateNextScreen = {
                lifecycleScope.launch {
                    val config = AppConfigManager.getInstance()
                    val shouldShowOnboarding =
                        !config.isOnboardingCompleted.getValue() || config.onboardReopen.getValue()
                    navigateNextScreen(isFromSetting, shouldShowOnboarding)
                }
            }
        )
    }

    fun navigateNextScreen(isFromSetting: Boolean, shouldShowOnboarding: Boolean) {
        val intent = when {
            isFromSetting -> Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            shouldShowOnboarding -> Intent(this, OnboardingActivity::class.java)
            /*isOpenPermission() -> {
                Intent(context, OnboardingActivity::class.java).apply {
                    putExtra(OPEN_PERMISSION, true)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }*/

            else -> Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate: LFOActivity")
    }
}

