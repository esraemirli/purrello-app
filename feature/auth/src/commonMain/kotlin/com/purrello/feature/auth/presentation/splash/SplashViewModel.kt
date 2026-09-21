package com.purrello.feature.auth.presentation.splash

import com.purrello.core.data.session.SessionRepository
import com.purrello.core.ui.mvi.MviViewModel

sealed interface SplashEffect {
    data object LoggedIn : SplashEffect
    data object LoggedOut : SplashEffect
}

/**
 * Start destination: reads the stored session and sends the user to Login or Main (navigation.md §6).
 * The read is a disk lookup, so the screen is a single frame — no progress state and no artificial wait.
 * When a network step joins the gate (token refresh, remote config), the SplashLoading artboard covers it.
 */
class SplashViewModel(
    private val session: SessionRepository,
) : MviViewModel<Unit, Nothing, SplashEffect>(Unit) {

    init {
        launch {
            sendEffect(if (session.isLoggedIn()) SplashEffect.LoggedIn else SplashEffect.LoggedOut)
        }
    }

    override fun onEvent(event: Nothing) = Unit
}
