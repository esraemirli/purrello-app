package com.purrello.feature.auth.presentation.login

import com.purrello.core.common.onFailure
import com.purrello.core.common.onSuccess
import com.purrello.core.ui.mvi.MviViewModel
import com.purrello.feature.auth.domain.AuthRepository
import com.purrello.feature.auth.domain.GoogleIdTokenProvider
import com.purrello.feature.auth.domain.GoogleSignInResult
import kotlinx.coroutines.Job

class LoginViewModel(
    private val googleIdTokenProvider: GoogleIdTokenProvider,
    private val authRepository: AuthRepository,
) : MviViewModel<LoginUiState, LoginEvent, LoginEffect>(LoginUiState()) {

    private var signInJob: Job? = null

    override fun onEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.GoogleSignInClicked -> signIn()
            LoginEvent.ErrorDialogRetryClicked -> {
                updateState { copy(errorDialog = null) }
                signIn()
            }
            LoginEvent.ErrorDialogDismissed -> updateState { copy(errorDialog = null) }
        }
    }

    private fun signIn() {
        if (signInJob?.isActive == true) return          // ignore double taps
        signInJob = launch {
            updateState { copy(isSigningIn = true) }
            when (val google = googleIdTokenProvider.requestIdToken()) {
                // User closed the Google sheet — show nothing (product rule).
                GoogleSignInResult.Cancelled -> updateState { copy(isSigningIn = false) }
                is GoogleSignInResult.Failed -> updateState { copy(isSigningIn = false, errorDialog = google.error) }
                is GoogleSignInResult.Success -> authRepository.signInWithGoogle(google.idToken)
                    .onSuccess { outcome ->
                        updateState { copy(isSigningIn = false) }
                        sendEffect(LoginEffect.SignedIn(outcome.isNewUser))
                    }
                    .onFailure { error -> updateState { copy(isSigningIn = false, errorDialog = error) } }
            }
        }
    }
}
