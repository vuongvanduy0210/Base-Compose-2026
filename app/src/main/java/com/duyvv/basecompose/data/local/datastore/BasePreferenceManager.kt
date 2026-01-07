package com.duyvv.basecompose.data.local.datastore

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duyvv.basecompose.data.local.datastore.BasePreferenceManager.PrefItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

abstract class BasePreferenceManager(private val dataStore: DataStore<Preferences>) {
    // Class Wrapper thông minh cho từng Preference item
    inner class PrefItem<T>(
        private val key: Preferences.Key<T>,
        val default: T
    ) {
        val asFlow: Flow<T> = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("DataStore", "Error reading preferences", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { it[key] ?: default }
            .flowOn(Dispatchers.IO)

        suspend fun getValue(): T = withContext(Dispatchers.IO) {
            try {
                dataStore.data.firstOrNull()?.get(key) ?: default
            } catch (e: Exception) {
                Log.e("DataStore", "Error getValue", e)
                default
            }
        }

        suspend fun set(value: T) = withContext(Dispatchers.IO) {
            try {
                dataStore.edit { it[key] = value }
            } catch (e: Exception) {
                Log.e("DataStore", "Error setting value", e)
            }
        }
    }

    protected fun booleanPref(key: Preferences.Key<Boolean>, default: Boolean = false) =
        PrefItem(key, default)

    protected fun intPref(key: Preferences.Key<Int>, default: Int = 0) =
        PrefItem(key, default)

    protected fun longPref(key: Preferences.Key<Long>, default: Long = 0L) =
        PrefItem(key, default)

    protected fun stringPref(key: Preferences.Key<String>, default: String = "") =
        PrefItem(key, default)
}

@Composable
fun <T> PrefItem<T>.collectAsState(initialValue: T?): State<T?> {
    return this.asFlow.collectAsStateWithLifecycle(initialValue = initialValue)
}