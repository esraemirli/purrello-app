package com.purrello.feature.auth.presentation.login

import androidx.compose.runtime.Immutable
import com.purrello.core.common.AppError

/** State the Screen renders. */
@Immutable
data class LoginUiState(
    val isSigningIn: Boolean = false,
    val errorDialog: AppError? = null,
)

/** User intents. The Screen sends these and nothing else. */
sealed interface LoginEvent {
    data object GoogleSignInClicked : LoginEvent
    data object ErrorDialogRetryClicked : LoginEvent
    data object ErrorDialogDismissed : LoginEvent
}

/** One-shot outcomes the parent owns (navigation). */
sealed interface LoginEffect {
    data class SignedIn(val isNewUser: Boolean) : LoginEffect
}
