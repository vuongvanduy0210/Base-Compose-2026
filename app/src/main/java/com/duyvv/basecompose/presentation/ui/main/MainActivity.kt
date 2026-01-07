package com.duyvv.basecompose.presentation.ui.main

import androidx.compose.runtime.Composable
import com.duyvv.basecompose.presentation.base.ComposeActivity

class MainActivity : ComposeActivity() {
    @Composable
    override fun Content() {
        MainScreen()
    }
}