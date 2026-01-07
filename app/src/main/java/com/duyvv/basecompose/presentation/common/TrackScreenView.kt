package com.duyvv.basecompose.presentation.common

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

@Composable
fun TrackingScreen(
    eventName: String,
    bundle: Bundle? = null
) {
    LaunchedEffect(Unit) {
        logEvent(eventName, bundle)
    }
}

@Composable
fun TrackingResumeScreen(
    eventName: String? = null,
    bundle: Bundle? = null
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentEventName by rememberUpdatedState(eventName)
    val currentBundle by rememberUpdatedState(bundle)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentEventName?.let { logEvent(it, currentBundle) }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

fun logEvent(event: String, bundle: Bundle? = null) {
    Log.d("event==", "$event ${bundle?.toString() ?: ""}")
    Firebase.analytics.logEvent(event, bundle)
}