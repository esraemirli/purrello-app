package com.purrello.core.data.session

import com.purrello.core.data.storage.SecureStore
import com.purrello.core.network.PurrelloJson
import com.purrello.core.network.auth.AuthTokens
import com.purrello.core.network.auth.TokenStorage
import kotlinx.serialization.SerializationException

internal class SecureTokenStorage(private val store: SecureStore) : TokenStorage {

    override suspend fun get(): AuthTokens? {
        val raw = store.getString(KEY) ?: return null
        return try {
            PurrelloJson.decodeFromString(AuthTokens.serializer(), raw)
        } catch (e: SerializationException) {
            store.remove(KEY)   // corrupted/old format → treat as logged out
            null
        }
    }

    override suspend fun save(tokens: AuthTokens) {
        store.putString(KEY, PurrelloJson.encodeToString(AuthTokens.serializer(), tokens))
    }

    override suspend fun clear() {
        store.remove(KEY)
    }

    private companion object {
        const val KEY = "auth_tokens"
    }
}
