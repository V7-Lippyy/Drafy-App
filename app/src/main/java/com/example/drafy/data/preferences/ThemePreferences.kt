// ThemePreferences.kt di package com.example.drafy.data.preferences
package com.example.drafy.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: false
    }

    suspend fun toggleDarkMode() {
        context.dataStore.edit { preferences ->
            val current = preferences[IS_DARK_MODE] ?: false
            preferences[IS_DARK_MODE] = !current
        }
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }
}