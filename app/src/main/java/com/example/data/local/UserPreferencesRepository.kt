package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.MimirThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mimir_prefs")

data class UserSettings(
    val themeMode: MimirThemeMode = MimirThemeMode.CARVED_WOOD_DARK,
    val lastDarkTheme: MimirThemeMode = MimirThemeMode.CARVED_WOOD_DARK,
    val lastLightTheme: MimirThemeMode = MimirThemeMode.PARCHMENT_LIGHT,
    val readerFontScale: Float = 1.0f,
    val parallelSideBySide: Boolean = true,
    val morningReminderEnabled: Boolean = false,
    val morningReminderHour: Int = 7,
    val biometricVaultEnabled: Boolean = false,
    val displayHandle: String = "Gestr"
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LAST_DARK_THEME = stringPreferencesKey("last_dark_theme")
        val LAST_LIGHT_THEME = stringPreferencesKey("last_light_theme")
        val FONT_SCALE = floatPreferencesKey("reader_font_scale")
        val PARALLEL_SIDE_BY_SIDE = booleanPreferencesKey("parallel_side_by_side")
        val MORNING_REMINDER = booleanPreferencesKey("morning_reminder_enabled")
        val MORNING_HOUR = intPreferencesKey("morning_reminder_hour")
        val BIOMETRIC_VAULT = booleanPreferencesKey("biometric_vault_enabled")
        val DISPLAY_HANDLE = stringPreferencesKey("display_handle")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        val themeName = prefs[Keys.THEME_MODE] ?: MimirThemeMode.CARVED_WOOD_DARK.name
        val darkName = prefs[Keys.LAST_DARK_THEME] ?: MimirThemeMode.CARVED_WOOD_DARK.name
        val lightName = prefs[Keys.LAST_LIGHT_THEME] ?: MimirThemeMode.PARCHMENT_LIGHT.name
        UserSettings(
            themeMode = runCatching { MimirThemeMode.valueOf(themeName) }
                .getOrDefault(MimirThemeMode.CARVED_WOOD_DARK),
            lastDarkTheme = runCatching { MimirThemeMode.valueOf(darkName) }
                .getOrDefault(MimirThemeMode.CARVED_WOOD_DARK),
            lastLightTheme = runCatching { MimirThemeMode.valueOf(lightName) }
                .getOrDefault(MimirThemeMode.PARCHMENT_LIGHT),
            readerFontScale = prefs[Keys.FONT_SCALE] ?: 1.0f,
            parallelSideBySide = prefs[Keys.PARALLEL_SIDE_BY_SIDE] ?: true,
            morningReminderEnabled = prefs[Keys.MORNING_REMINDER] ?: false,
            morningReminderHour = prefs[Keys.MORNING_HOUR] ?: 7,
            biometricVaultEnabled = prefs[Keys.BIOMETRIC_VAULT] ?: false,
            displayHandle = prefs[Keys.DISPLAY_HANDLE] ?: "Gestr"
        )
    }

    suspend fun setThemeMode(mode: MimirThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
            if (mode.isDark) {
                prefs[Keys.LAST_DARK_THEME] = mode.name
            } else {
                prefs[Keys.LAST_LIGHT_THEME] = mode.name
            }
        }
    }

    suspend fun toggleDarkMode() {
        val current = settingsFlow.first()
        val target = if (current.themeMode.isDark) current.lastLightTheme else current.lastDarkTheme
        setThemeMode(target)
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { it[Keys.FONT_SCALE] = scale.coerceIn(0.85f, 1.45f) }
    }

    suspend fun setParallelSideBySide(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PARALLEL_SIDE_BY_SIDE] = enabled }
    }

    suspend fun setMorningReminder(enabled: Boolean, hour: Int) {
        context.dataStore.edit {
            it[Keys.MORNING_REMINDER] = enabled
            it[Keys.MORNING_HOUR] = hour
        }
    }

    suspend fun setBiometricVaultEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BIOMETRIC_VAULT] = enabled }
    }

    suspend fun setDisplayHandle(handle: String) {
        context.dataStore.edit { it[Keys.DISPLAY_HANDLE] = handle.ifBlank { "Gestr" } }
    }

    suspend fun restoreFromSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = settings.themeMode.name
            prefs[Keys.LAST_DARK_THEME] = settings.lastDarkTheme.name
            prefs[Keys.LAST_LIGHT_THEME] = settings.lastLightTheme.name
            prefs[Keys.FONT_SCALE] = settings.readerFontScale
            prefs[Keys.PARALLEL_SIDE_BY_SIDE] = settings.parallelSideBySide
            prefs[Keys.MORNING_REMINDER] = settings.morningReminderEnabled
            prefs[Keys.MORNING_HOUR] = settings.morningReminderHour
            prefs[Keys.BIOMETRIC_VAULT] = settings.biometricVaultEnabled
            prefs[Keys.DISPLAY_HANDLE] = settings.displayHandle
        }
    }
}
