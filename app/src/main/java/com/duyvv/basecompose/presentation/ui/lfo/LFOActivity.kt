package com.duyvv.basecompose.presentation.ui.lfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import com.duyvv.basecompose.presentation.base.ComposeActivity
import com.duyvv.basecompose.presentation.ui.main.MainActivity
import com.duyvv.basecompose.presentation.ui.onboarding.OnboardingActivity
import com.duyvv.basecompose.utils.isOpenOnboarding

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
                val intent = when {
                    isFromSetting -> Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }

                    isOpenOnboarding() -> Intent(this, OnboardingActivity::class.java)

                    else -> Intent(this, MainActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate: LFOActivity")
    }
}

