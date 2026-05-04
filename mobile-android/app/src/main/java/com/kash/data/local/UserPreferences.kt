package com.kash.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.kash.domain.model.UserSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN     = stringPreferencesKey("token")
        private val USER_ID   = stringPreferencesKey("user_id")
        private val ORG_ID    = stringPreferencesKey("org_id")
        private val WALLET_ID = stringPreferencesKey("wallet_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL= stringPreferencesKey("user_email")
    }

    val session: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        val token = prefs[TOKEN] ?: return@map null
        UserSession(
            token          = token,
            userId         = prefs[USER_ID]    ?: "",
            orgId          = prefs[ORG_ID]     ?: "",
            activeWalletId = prefs[WALLET_ID]  ?: "",
            userName       = prefs[USER_NAME]  ?: "",
            userEmail      = prefs[USER_EMAIL] ?: ""
        )
    }

    suspend fun save(session: UserSession) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN]     = session.token
            prefs[USER_ID]   = session.userId
            prefs[ORG_ID]    = session.orgId
            prefs[WALLET_ID] = session.activeWalletId
            prefs[USER_NAME] = session.userName
            prefs[USER_EMAIL]= session.userEmail
        }
    }

    suspend fun clear() = context.dataStore.edit { it.clear() }
}
