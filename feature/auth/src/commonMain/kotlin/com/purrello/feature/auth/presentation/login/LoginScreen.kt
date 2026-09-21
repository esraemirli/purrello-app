package com.purrello.feature.auth.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.purrello.core.designsystem.component.PurrBottomActionBar
import com.purrello.core.designsystem.component.PurrButton
import com.purrello.core.designsystem.theme.PurrelloTheme
import com.purrello.core.ui.error.AppErrorDialog
import com.purrello.core.ui.mvi.ObserveEffects
import com.purrello.core.ui.resources.common_brand_wordmark
import com.purrello.feature.auth.resources.Res
import com.purrello.feature.auth.resources.auth_login_cta_google
import com.purrello.feature.auth.resources.auth_login_subtitle
import com.purrello.feature.auth.resources.auth_login_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.purrello.core.ui.resources.Res as CoreRes

@Composable
fun LoginRoute(
    onSignedIn: (isNewUser: Boolean) -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            is LoginEffect.SignedIn -> onSignedIn(effect.isNewUser)
        }
    }
    LoginScreen(state = state, onEvent = viewModel::onEvent)
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = PurrelloTheme.spacing
    Column(modifier = modifier.fillMaxSize().background(PurrelloTheme.colors.bgHero)) {
        // TODO(ds): PurrBrandHero with tone-on-tone paw prints (#F5ABA7).
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = spacing.screenEdge),
            verticalArrangement = Arrangement.spacedBy(spacing.md, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(CoreRes.string.common_brand_wordmark),
                style = PurrelloTheme.typography.wordmark,
                color = PurrelloTheme.colors.actionPrimary,
            )
            Text(
                text = stringResource(Res.string.auth_login_title),
                style = PurrelloTheme.typography.display,
                color = PurrelloTheme.colors.textPrimary,
            )
            Text(
                text = stringResource(Res.string.auth_login_subtitle),
                style = PurrelloTheme.typography.bodyLg,
                color = PurrelloTheme.colors.heroText2,
                textAlign = TextAlign.Center,
            )
        }
        PurrBottomActionBar {
            // TODO(ds): official Google "G" asset + Google branding guidelines.
            PurrButton(
                text = stringResource(Res.string.auth_login_cta_google),
                onClick = { onEvent(LoginEvent.GoogleSignInClicked) },
                loading = state.isSigningIn,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    state.errorDialog?.let { error ->
        AppErrorDialog(
            error = error,
            onRetry = { onEvent(LoginEvent.ErrorDialogRetryClicked) },
            onDismiss = { onEvent(LoginEvent.ErrorDialogDismissed) },
        )
    }
}
