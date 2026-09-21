package com.purrello.core.ui.mvi

import androidx.compose.runtime.Immutable
import com.purrello.core.common.AppError
import com.purrello.core.common.AppResult

/**
 * Lifecycle of one asynchronously loaded piece of state (architecture.md §4).
 *
 * One field instead of `screenState` + nullable `content`, so impossible combinations (Content with a
 * null content, Loading that lost the data it was refreshing) can't be represented.
 *
 * [Loading] and [Failure] carry [previous] — the data that is still on screen — which is exactly what
 * decides the error surface: `previous == null` → full-screen error, otherwise content + retry dialog
 * (ui-conventions.md §3).
 */
@Immutable
sealed interface Async<out T> {
    /** Nothing requested yet (initial state). */
    data object Uninitialized : Async<Nothing>

    data class Loading<out T>(val previous: T? = null) : Async<T>

    data class Success<out T>(val value: T) : Async<T>

    data class Failure<out T>(val error: AppError, val previous: T? = null) : Async<T>
}

/** Data currently renderable, if any (kept while refreshing or after a failed refresh). */
val <T> Async<T>.valueOrNull: T?
    get() = when (this) {
        is Async.Success -> value
        is Async.Loading -> previous
        is Async.Failure -> previous
        Async.Uninitialized -> null
    }

val Async<*>.isLoading: Boolean get() = this is Async.Loading

/** Loading with data already on screen — drives pull-to-refresh / inline progress, not a full-screen spinner. */
val Async<*>.isRefreshing: Boolean get() = this is Async.Loading && previous != null

val Async<*>.errorOrNull: AppError? get() = (this as? Async.Failure)?.error

/** Start (or restart) a load, keeping whatever is on screen. */
fun <T> Async<T>.toLoading(): Async<T> = Async.Loading(valueOrNull)

/** Fail, keeping whatever is on screen so the UI can pick dialog vs full-screen error. */
fun <T> Async<T>.toFailure(error: AppError): Async<T> = Async.Failure(error, valueOrNull)

/** User dismissed the retry dialog: keep the old data, drop the error. Full-screen errors stay. */
fun <T> Async<T>.dismissFailure(): Async<T> =
    (this as? Async.Failure)?.previous?.let { Async.Success(it) } ?: this

fun <T, R> Async<T>.map(transform: (T) -> R): Async<R> = when (this) {
    Async.Uninitialized -> Async.Uninitialized
    is Async.Loading -> Async.Loading(previous?.let(transform))
    is Async.Success -> Async.Success(transform(value))
    is Async.Failure -> Async.Failure(error, previous?.let(transform))
}

/** `repository.getX().toAsync(currentState.content)` — one line from AppResult to Async. */
fun <T> AppResult<T>.toAsync(previous: Async<T> = Async.Uninitialized): Async<T> = when (this) {
    is AppResult.Success -> Async.Success(value)
    is AppResult.Failure -> previous.toFailure(error)
}
