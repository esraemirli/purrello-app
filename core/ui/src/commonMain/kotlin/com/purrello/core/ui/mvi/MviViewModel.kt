package com.purrello.core.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI host for one screen (architecture.md §3):
 *
 * - **State**: one immutable `@Immutable` data class, the single source of truth the Screen renders.
 * - **Event**: a user intent from the Screen (`sealed interface FooEvent`). The Screen only calls [onEvent].
 * - **Effect**: a one-shot, parent-owned outcome (navigate, close, open share sheet). Never UI that must survive
 *   a configuration change / process death — that is State.
 *
 * Effects go through a buffered [Channel], so nothing is dropped while the Route is off-screen
 * (a `SharedFlow` would drop them). Exactly one collector: `ObserveEffects` in the Route.
 *
 * Plain Kotlin + coroutines: works identically on Android and iOS.
 */
abstract class MviViewModel<State, Event, Effect>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    /** Current state — for reading inside event handling, never for rendering. */
    protected val currentState: State get() = _state.value

    private val _effects = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    /** The only entry point from the UI. Implement with an exhaustive `when (event)`. */
    abstract fun onEvent(event: Event)

    protected fun updateState(reducer: State.() -> State) {
        _state.update(reducer)
    }

    protected fun sendEffect(effect: Effect) {
        _effects.trySend(effect)
    }

    /** `viewModelScope.launch` — cancelled when the screen's back-stack entry is popped. */
    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job = viewModelScope.launch(block = block)
}
