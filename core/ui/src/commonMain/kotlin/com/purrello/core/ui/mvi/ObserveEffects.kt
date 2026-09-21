package com.purrello.core.ui.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Collects a ViewModel's one-shot [MviViewModel.effects] in a Route and maps them to navigation lambdas.
 * Main.immediate so no effect is lost between recompositions. Only Routes call this — never Screens.
 */
@Composable
fun <T> ObserveEffects(
    effects: Flow<T>,
    key: Any? = null,
    onEffect: (T) -> Unit,
) {
    val currentOnEffect by rememberUpdatedState(onEffect)
    LaunchedEffect(effects, key) {
        withContext(Dispatchers.Main.immediate) {
            effects.collect { currentOnEffect(it) }
        }
    }
}
