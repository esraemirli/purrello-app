package com.purrello.core.data.session

import com.purrello.core.data.pet.SelectedPetRepository
import com.purrello.core.network.auth.AuthTokens
import com.purrello.core.network.auth.SessionEvents
import com.purrello.core.network.auth.TokenStorage
import com.purrello.core.network.clearCachedTokens
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow

/** Owns the logged-in state. Features call this; they never touch tokens directly. */
interface SessionRepository {
    suspend fun isLoggedIn(): Boolean
    suspend fun startSession(tokens: AuthTokens)
    suspend fun endSession()

    /** Refresh failed — the app root sends the user back to Login. */
    val sessionExpired: Flow<Unit>
}

internal class DefaultSessionRepository(
    private val tokenStorage: TokenStorage,
    private val sessionEvents: SessionEvents,
    private val selectedPet: SelectedPetRepository,
    private val httpClient: HttpClient,
) : SessionRepository {

    override val sessionExpired: Flow<Unit> = sessionEvents.expired

    override suspend fun isLoggedIn(): Boolean = tokenStorage.get() != null

    override suspend fun startSession(tokens: AuthTokens) {
        tokenStorage.save(tokens)
        httpClient.clearCachedTokens()
    }

    override suspend fun endSession() {
        tokenStorage.clear()
        selectedPet.clear()
        httpClient.clearCachedTokens()
    }
}
