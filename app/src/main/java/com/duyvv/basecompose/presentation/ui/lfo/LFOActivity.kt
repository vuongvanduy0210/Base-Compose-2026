package com.duyvv.basecompose.presentation.ui.lfo

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import com.duyvv.basecompose.presentation.base.ComposeActivity

class LFOActivity : ComposeActivity() {

    @Composable
    override fun Content() {
        val isFromSetting = intent.getBooleanExtra("open_from_setting", false)
        LFOScreen(
            isFromSetting = isFromSetting,
            onClickBack = {
                finish()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate: LFOActivity")
    }
}

