package com.purrello.core.testing

import com.purrello.core.data.change.DataChange
import com.purrello.core.data.change.DataChangeNotifier
import com.purrello.core.data.pet.SelectedPetRepository
import com.purrello.core.data.session.SessionRepository
import com.purrello.core.model.PetId
import com.purrello.core.network.auth.AuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSelectedPetRepository(initial: PetId? = null) : SelectedPetRepository {
    private val _selected = MutableStateFlow(initial)
    override val selectedPetId: StateFlow<PetId?> = _selected.asStateFlow()
    override fun select(petId: PetId) { _selected.value = petId }
    override fun clear() { _selected.value = null }
}

class FakeDataChangeNotifier : DataChangeNotifier {
    private val _changes = MutableSharedFlow<DataChange>(extraBufferCapacity = 64)
    override val changes: SharedFlow<DataChange> = _changes.asSharedFlow()
    val notified = mutableListOf<DataChange>()
    override fun notify(change: DataChange) {
        notified += change
        _changes.tryEmit(change)
    }
}

class FakeSessionRepository(var loggedIn: Boolean = false) : SessionRepository {
    val startedSessions = mutableListOf<AuthTokens>()
    var endSessionCalls = 0
    private val _expired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val sessionExpired: Flow<Unit> = _expired

    override suspend fun isLoggedIn(): Boolean = loggedIn
    override suspend fun startSession(tokens: AuthTokens) { startedSessions += tokens; loggedIn = true }
    override suspend fun endSession() { endSessionCalls++; loggedIn = false }
    fun expire() { _expired.tryEmit(Unit) }
}
