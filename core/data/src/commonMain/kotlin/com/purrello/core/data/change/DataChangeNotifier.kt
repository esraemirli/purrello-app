package com.purrello.core.data.change

import com.purrello.core.model.PetId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance

/**
 * What changed after a successful mutation. Topics are defined here (not in features) because the
 * producer and the consumer are often different features (e.g. Health mutation → Home refresh).
 */
sealed interface DataChange {
    data object Pets : DataChange
    data class Health(val petId: PetId) : DataChange
    data class Documents(val petId: PetId) : DataChange
    data class Care(val petId: PetId) : DataChange
    data class LostReport(val petId: PetId) : DataChange
    data object Account : DataChange
}

/**
 * Repositories call [notify] after a mutation succeeds; ViewModels showing that data observe the topic
 * and refresh silently (architecture.md §2). Live-only by design: a screen that isn't alive reloads
 * fresh data when it is created anyway.
 */
interface DataChangeNotifier {
    val changes: SharedFlow<DataChange>
    fun notify(change: DataChange)
}

inline fun <reified T : DataChange> DataChangeNotifier.observe(): Flow<T> = changes.filterIsInstance<T>()

class DefaultDataChangeNotifier : DataChangeNotifier {
    private val _changes = MutableSharedFlow<DataChange>(extraBufferCapacity = 64)
    override val changes: SharedFlow<DataChange> = _changes.asSharedFlow()

    override fun notify(change: DataChange) {
        _changes.tryEmit(change)
    }
}
