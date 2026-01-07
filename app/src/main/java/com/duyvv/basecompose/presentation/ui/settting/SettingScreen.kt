package com.duyvv.basecompose.presentation.ui.settting

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.duyvv.basecompose.presentation.common.composeview.AppText

@Composable
fun SettingScreen(modifier: Modifier = Modifier) {
    BackHandler(enabled = true) {
        Log.d("TAG+++", "MainNavigationRoot: back click")
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AppText(text = "Setting Screen", fontSize = 20.sp)
    }
}