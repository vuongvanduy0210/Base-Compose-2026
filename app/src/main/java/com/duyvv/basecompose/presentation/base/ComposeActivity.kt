package com.duyvv.basecompose.presentation.base

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.presentation.ui.theme.MoneyTrackerTheme
import com.duyvv.basecompose.utils.LanguageUtils
import kotlinx.coroutines.runBlocking

abstract class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        init()
        setContent {
            MoneyTrackerTheme {
                Content()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val languageCode = try {
            runBlocking {
                AppConfigManager.getInstance().languageCode.getValue()
            }
        } catch (e: Exception) {
            "en"
        }
        val context = LanguageUtils.getLocalizedContext(newBase, languageCode)
        super.attachBaseContext(context)
    }

    @Composable
    abstract fun Content()

    open fun init() {}

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val attributes = window.attributes
            attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = attributes
        }
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }
}