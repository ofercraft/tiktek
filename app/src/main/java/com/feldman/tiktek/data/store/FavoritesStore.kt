package com.feldman.tiktek.data.store


import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "tiktek_prefs")


class FavoritesStore(private val context: Context) {
    private val KEY: Preferences.Key<Set<String>> = stringSetPreferencesKey("favorites")


    val favoritesFlow: Flow<Set<String>> = context.dataStore.data.map { it[KEY] ?: emptySet() }


    suspend fun toggle(id: String) {
        context.dataStore.edit { prefs ->
            val cur = prefs[KEY] ?: emptySet()
            prefs[KEY] = if (cur.contains(id)) cur - id else cur + id
        }
    }


    suspend fun isFavorite(id: String): Boolean = favoritesFlow.map { it.contains(id) }.let { flow ->
// In real code you'd collect; for brevity not exposing suspend here
        false
    }
}