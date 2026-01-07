package com.duyvv.basecompose.utils

import android.util.Log
import kotlin.random.Random

fun shouldShowAdOpenAOA(mediumPercentage: Int): Boolean {
    require(mediumPercentage in 0..100) {
        "Medium percentage must be between 0 and 100, got: $mediumPercentage"
    }
    return when (mediumPercentage) {
        0 -> false
        100 -> true
        else -> {
            val random = Random.nextInt(100)
            Log.d("AppOpenId", "ConfigRateAOA: configRate: $mediumPercentage, result: $random")
            random < mediumPercentage
        }
    }
}