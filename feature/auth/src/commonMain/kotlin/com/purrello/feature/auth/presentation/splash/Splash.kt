package com.purrello.feature.auth.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.purrello.core.data.session.SessionRepository
import com.purrello.core.designsystem.theme.PurrelloTheme
import com.purrello.core.ui.mvi.MviViewModel
import com.purrello.core.ui.mvi.ObserveEffects
import com.purrello.core.ui.resources.Res
import com.purrello.core.ui.resources.common_brand_wordmark
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

sealed interface SplashEffect {
    data object LoggedIn : SplashEffect
    data object LoggedOut : SplashEffect
}

/** Start destination: decides Login vs Main from the stored session (navigation.md §6). No user events. */
class SplashViewModel(
    private val session: SessionRepository,
) : MviViewModel<Unit, Nothing, SplashEffect>(Unit) {

    init {
        launch {
            sendEffect(if (session.isLoggedIn()) SplashEffect.LoggedIn else SplashEffect.LoggedOut)
        }
    }

    override fun onEvent(event: Nothing) = Unit      // this screen has no user interaction
}

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

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(PurrelloTheme.colors.bgBrand),
        contentAlignment = Alignment.Center,
    ) {
        // The logo is the only white-on-coral text allowed (design-system.md).
        Text(
            text = stringResource(Res.string.common_brand_wordmark),
            style = PurrelloTheme.typography.wordmark,
            color = PurrelloTheme.colors.bgSurface,
        )
    }
}
