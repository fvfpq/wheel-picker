package com.example.wheelpicker.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wheelpicker.data.model.SpinRecord
import com.example.wheelpicker.data.model.WheelConfig
import com.example.wheelpicker.data.model.WheelOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "wheel_picker")

class OptionRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private val keys = PreferenceKeys

    val config: Flow<WheelConfig> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs -> decode(prefs[keys.CONFIG_JSON]) }

    val history: Flow<List<SpinRecord>> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            prefs[keys.HISTORY_JSON]
                ?.let { raw -> runCatching { json.decodeFromString<List<SpinRecord>>(raw) }.getOrNull() }
                ?: emptyList()
        }

    suspend fun saveConfig(config: WheelConfig) {
        context.dataStore.edit { prefs ->
            prefs[keys.CONFIG_JSON] = json.encodeToString(WheelConfig.serializer(), config.normalized())
        }
    }

    suspend fun updateOptions(options: List<WheelOption>) {
        context.dataStore.edit { prefs ->
            val current = decode(prefs[keys.CONFIG_JSON])
            prefs[keys.CONFIG_JSON] = json.encodeToString(
                WheelConfig.serializer(),
                current.copy(options = options.map { it.normalized() }).normalized()
            )
        }
    }

    suspend fun updatePassword(password: String) {
        context.dataStore.edit { prefs ->
            val current = decode(prefs[keys.CONFIG_JSON])
            prefs[keys.CONFIG_JSON] = json.encodeToString(
                WheelConfig.serializer(),
                current.copy(password = password).normalized()
            )
        }
    }

    suspend fun setForcedOption(optionId: String?) {
        context.dataStore.edit { prefs ->
            val current = decode(prefs[keys.CONFIG_JSON])
            prefs[keys.CONFIG_JSON] = json.encodeToString(
                WheelConfig.serializer(),
                current.copy(forcedOptionId = optionId).normalized()
            )
        }
    }

    suspend fun addRecord(record: SpinRecord) {
        context.dataStore.edit { prefs ->
            val current = prefs[keys.HISTORY_JSON]
                ?.let { raw -> runCatching { json.decodeFromString<List<SpinRecord>>(raw) }.getOrNull() }
                ?: emptyList()
            prefs[keys.HISTORY_JSON] = json.encodeToString(
                ListSerializerHolder.records,
                (listOf(record) + current).take(200)
            )
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { prefs ->
            prefs[keys.HISTORY_JSON] = json.encodeToString(
                ListSerializerHolder.records,
                emptyList<SpinRecord>()
            )
        }
    }

    private fun decode(raw: String?): WheelConfig {
        return raw
            ?.let { runCatching { json.decodeFromString(WheelConfig.serializer(), it) }.getOrNull() }
            ?.normalized()
            ?: WheelConfig()
    }
}

private object ListSerializerHolder {
    val records = kotlinx.serialization.builtins.ListSerializer(SpinRecord.serializer())
}

private object PreferenceKeys {
    val CONFIG_JSON = stringPreferencesKey("wheel_config")
    val HISTORY_JSON = stringPreferencesKey("spin_history")
}
