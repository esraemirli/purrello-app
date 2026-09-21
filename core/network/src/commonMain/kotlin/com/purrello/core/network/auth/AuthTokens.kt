package com.purrello.core.network.auth

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokens(val accessToken: String, val refreshToken: String)

/** Persists tokens. Implemented in core:data on top of SecureStore. */
interface TokenStorage {
    suspend fun get(): AuthTokens?
    suspend fun save(tokens: AuthTokens)
    suspend fun clear()
}

/**
 * Global "session is over" signal (refresh failed). The app root listens and resets navigation to Login.
 * Live-only on purpose: if nothing is listening, the next app start sees no tokens anyway.
 */
class SessionEvents {
    private val _expired = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val expired: Flow<Unit> = _expired.asSharedFlow()

    fun notifyExpired() {
        _expired.tryEmit(Unit)
    }
}

@Serializable
internal data class RefreshTokenRequest(val refreshToken: String)

@Serializable
internal data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long,
)
