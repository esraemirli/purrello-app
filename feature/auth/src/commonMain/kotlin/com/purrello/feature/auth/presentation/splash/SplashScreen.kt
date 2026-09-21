package com.purrello.feature.auth.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purrello.core.designsystem.theme.PurrelloTheme
import com.purrello.core.ui.mvi.ObserveEffects
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashRoute(
    onLoggedIn: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: SplashViewModel = koinViewModel(),
) {
    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            SplashEffect.LoggedIn -> onLoggedIn()
            SplashEffect.LoggedOut -> onLoggedOut()
        }
    }
    SplashScreen()
}

/**
 * The brand moment belongs to the platform splash (Android: `Theme.Purrello.Splash`), so this route only
 * holds the session gate's single frame. It paints `bgHero` — the same colour the platform splash uses —
 * so the handover to Login or Main is invisible instead of a second splash.
 */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(PurrelloTheme.colors.bgHero))
}
