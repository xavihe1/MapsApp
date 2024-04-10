package com.example.mapsapp.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.remote.Datastore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

class UserPrefs(private val context: Context) {

    //CREATE A DATASTORE
    companion object {
        private val Context.dataStore: Datastore<Preferences> by preferencesDataStore(name = "user_prefs")
        val STORE_USERNAME = stringPreferencesKey("store_username")
        val STORE_USERPASS = stringPreferencesKey("store_userpass")
    }

    //GET THE USER DATA
    val getUserData: Flow<List<String>> = context.dataStore.data.map { prefs ->
        listOf(
            prefs[STORE_USERNAME] ?: "",
            prefs[STORE_USERPASS] ?: ""
        )
    }

    //SAVE THE USER DATA
    suspend fun saveUserData(username: String, userpass: String) {
        context.dataStore.edit { prefs ->
            prefs[STORE_USERNAME] = username
            prefs[STORE_USERPASS] = userpass
        }
    }
}