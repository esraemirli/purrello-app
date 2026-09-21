package com.purrello.core.ui.mvi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purrello.core.designsystem.component.PurrLoadingState
import com.purrello.core.ui.error.AppErrorDialog
import com.purrello.core.ui.error.AppErrorState

/**
 * Renders one [Async] slice with the product's error rules baked in (ui-conventions.md §3):
 *
 * | Async state | UI |
 * |---|---|
 * | `Uninitialized`, `Loading(previous = null)` | [loading] (full-area spinner by default) |
 * | `Success`, `Loading(previous)` | [content] — data stays visible while refreshing |
 * | `Failure(previous = null)` | full-screen `AppErrorState` with "Tekrar dene" |
 * | `Failure(previous)` | [content] + `AppErrorDialog` ("Tekrar dene" inside, close keeps the data) |
 *
 * No toasts anywhere. [onErrorDismissed] should map to an Event that calls `dismissFailure()`.
 */
@Composable
fun <T> AsyncContent(
    state: Async<T>,
    onRetry: () -> Unit,
    onErrorDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    bodyOverride: String? = null,
    loading: @Composable () -> Unit = { PurrLoadingState(modifier) },
    content: @Composable (T) -> Unit,
) {
    val value = state.valueOrNull
    when {
        value != null -> content(value)
        state is Async.Failure -> AppErrorState(
            error = state.error,
            onRetry = onRetry,
            modifier = modifier,
            bodyOverride = bodyOverride,
        )
        else -> loading()
    }

    if (value != null && state is Async.Failure) {
        AppErrorDialog(
            error = state.error,
            onRetry = onRetry,
            onDismiss = onErrorDismissed,
            bodyOverride = bodyOverride,
        )
    }
}
