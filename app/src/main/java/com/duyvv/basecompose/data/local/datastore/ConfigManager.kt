package com.duyvv.basecompose.data.local.datastore

import android.util.Log
import com.duyvv.basecompose.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ConfigManager {
    suspend fun loadRemoteConfig(): Boolean = withContext(Dispatchers.IO) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        try {
            val fetchSuccessful = remoteConfig.fetchAndActivate().await()
            applyRemoteConfig(remoteConfig)
            Log.e("ConfigManager", "loadRemoteConfig: Success (Updated: $fetchSuccessful)")
            true
        } catch (e: Exception) {
            Log.e("ConfigManager", "Remote config fetch failed: ${e.message}", e)
            true
        }
    }

    private suspend fun applyRemoteConfig(remoteConfig: FirebaseRemoteConfig) {
        with(AppConfigManager.Companion.getInstance()) {

            Log.d("SplashViewModel", "applyRemoteConfig complete")
        }
    }
}