package com.purrello.feature.account.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.purrello.core.data.session.SessionRepository
import com.purrello.core.designsystem.component.PurrBottomActionBar
import com.purrello.core.designsystem.component.PurrButton
import com.purrello.core.designsystem.component.PurrButtonStyle
import com.purrello.core.designsystem.component.PurrDetailBar
import com.purrello.core.designsystem.theme.PurrelloTheme
import com.purrello.core.ui.component.ComingSoonContent
import com.purrello.core.ui.mvi.MviViewModel
import com.purrello.core.ui.mvi.ObserveEffects
import com.purrello.core.ui.resources.common_action_back
import com.purrello.feature.account.resources.Res
import com.purrello.feature.account.resources.account_profile_sign_out
import com.purrello.feature.account.resources.account_profile_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.purrello.core.ui.resources.Res as CoreRes

@Immutable
data class OwnerProfileUiState(val isSigningOut: Boolean = false)

sealed interface OwnerProfileEvent {
    data object BackClicked : OwnerProfileEvent
    data object SignOutClicked : OwnerProfileEvent
}

sealed interface OwnerProfileEffect {
    data object NavigateBack : OwnerProfileEffect
    data object SignedOut : OwnerProfileEffect
}

/** Owner profile — sign-out lives at the bottom of the content, never in the top bar. */
class OwnerProfileViewModel(
    private val session: SessionRepository,
) : MviViewModel<OwnerProfileUiState, OwnerProfileEvent, OwnerProfileEffect>(OwnerProfileUiState()) {

    override fun onEvent(event: OwnerProfileEvent) {
        when (event) {
            OwnerProfileEvent.BackClicked -> sendEffect(OwnerProfileEffect.NavigateBack)
            OwnerProfileEvent.SignOutClicked -> signOut()
        }
    }

    private fun signOut() {
        if (currentState.isSigningOut) return
        launch {
            updateState { copy(isSigningOut = true) }
            // TODO(api): POST /v1/auth/logout (docs/api/common/auth.md §2.3) — local tokens are cleared regardless.
            session.endSession()
            sendEffect(OwnerProfileEffect.SignedOut)
        }
    }
}

@Composable
fun OwnerProfileRoute(
    onBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: OwnerProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            OwnerProfileEffect.NavigateBack -> onBack()
            OwnerProfileEffect.SignedOut -> onSignedOut()
        }
    }
    OwnerProfileScreen(state = state, onEvent = viewModel::onEvent)
}

@Composable
fun OwnerProfileScreen(
    state: OwnerProfileUiState,
    onEvent: (OwnerProfileEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(PurrelloTheme.colors.bgPage)) {
        PurrDetailBar(
            title = stringResource(Res.string.account_profile_title),
            backContentDescription = stringResource(CoreRes.string.common_action_back),
            onBack = { onEvent(OwnerProfileEvent.BackClicked) },
        )
        Box(modifier = Modifier.weight(1f)) { ComingSoonContent() }
        PurrBottomActionBar {
            PurrButton(
                text = stringResource(Res.string.account_profile_sign_out),
                onClick = { onEvent(OwnerProfileEvent.SignOutClicked) },
                style = PurrButtonStyle.DANGER,
                loading = state.isSigningOut,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
