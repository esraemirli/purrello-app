package com.purrello.feature.auth.presentation.login

import androidx.compose.runtime.Composable
import com.purrello.core.common.AppError
import com.purrello.core.designsystem.preview.PurrPreviews
import com.purrello.core.designsystem.theme.PurrelloTheme

@PurrPreviews
@Composable
private fun LoginScreenPreview() {
    PurrelloTheme { LoginScreen(state = LoginUiState(), onEvent = {}) }
}

@PurrPreviews
@Composable
private fun LoginScreenSigningInPreview() {
    PurrelloTheme { LoginScreen(state = LoginUiState(isSigningIn = true), onEvent = {}) }
}

@PurrPreviews
@Composable
private fun LoginScreenNoConnectionPreview() {
    PurrelloTheme { LoginScreen(state = LoginUiState(errorDialog = AppError.NoConnection), onEvent = {}) }
}
