package com.purrello.feature.auth

import app.cash.turbine.testIn
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.purrello.core.common.AppError
import com.purrello.core.common.AppResult
import com.purrello.core.testing.MainDispatcherTest
import com.purrello.feature.auth.domain.AuthRepository
import com.purrello.feature.auth.domain.GoogleIdTokenProvider
import com.purrello.feature.auth.domain.GoogleSignInResult
import com.purrello.feature.auth.domain.SignInOutcome
import com.purrello.feature.auth.presentation.login.LoginEffect
import com.purrello.feature.auth.presentation.login.LoginEvent
import com.purrello.feature.auth.presentation.login.LoginUiState
import com.purrello.feature.auth.presentation.login.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class LoginViewModelTest : MainDispatcherTest() {

    private val google = FakeGoogle()
    private val repository = FakeAuthRepository()

    private fun viewModel() = LoginViewModel(google, repository)

    @Test
    fun `GIVEN google and backend succeed WHEN sign in clicked THEN loading then signed-in effect`() = runTest(testDispatcher) {
        google.result = GoogleSignInResult.Success("id-token")
        repository.result = AppResult.Success(SignInOutcome(isNewUser = true))
        val vm = viewModel()
        val states = vm.state.testIn(backgroundScope)
        val effects = vm.effects.testIn(backgroundScope)

        vm.onEvent(LoginEvent.GoogleSignInClicked)

        assertThat(states.awaitItem()).isEqualTo(LoginUiState())
        assertThat(states.awaitItem()).isEqualTo(LoginUiState(isSigningIn = true))
        assertThat(states.awaitItem()).isEqualTo(LoginUiState(isSigningIn = false))
        assertThat(effects.awaitItem()).isEqualTo(LoginEffect.SignedIn(isNewUser = true))
        assertThat(repository.idTokens).isEqualTo(listOf("id-token"))
        states.cancel()
        effects.cancel()
    }

    @Test
    fun `GIVEN user cancels google sheet WHEN sign in clicked THEN nothing is shown`() = runTest(testDispatcher) {
        google.result = GoogleSignInResult.Cancelled
        val vm = viewModel()
        val states = vm.state.testIn(backgroundScope)
        val effects = vm.effects.testIn(backgroundScope)

        vm.onEvent(LoginEvent.GoogleSignInClicked)

        assertThat(states.awaitItem()).isEqualTo(LoginUiState())
        assertThat(states.awaitItem()).isEqualTo(LoginUiState(isSigningIn = true))
        assertThat(states.awaitItem()).isEqualTo(LoginUiState(isSigningIn = false))
        effects.expectNoEvents()
        assertThat(repository.idTokens).isEqualTo(emptyList())
        states.cancel()
        effects.cancel()
    }

    @Test
    fun `GIVEN backend unreachable WHEN sign in clicked THEN error dialog`() = runTest(testDispatcher) {
        google.result = GoogleSignInResult.Success("id-token")
        repository.result = AppResult.Failure(AppError.NoConnection)
        val vm = viewModel()
        val states = vm.state.testIn(backgroundScope)

        vm.onEvent(LoginEvent.GoogleSignInClicked)

        assertThat(states.awaitItem()).isEqualTo(LoginUiState())
        assertThat(states.awaitItem()).isEqualTo(LoginUiState(isSigningIn = true))
        assertThat(states.awaitItem()).isEqualTo(LoginUiState(errorDialog = AppError.NoConnection))
        states.cancel()
    }

    @Test
    fun `GIVEN error dialog WHEN retry clicked THEN dialog closes and sign in runs again`() = runTest(testDispatcher) {
        google.result = GoogleSignInResult.Success("id-token")
        repository.result = AppResult.Failure(AppError.NoConnection)
        val vm = viewModel()
        vm.onEvent(LoginEvent.GoogleSignInClicked)
        testScheduler.advanceUntilIdle()
        repository.result = AppResult.Success(SignInOutcome(isNewUser = false))
        val effects = vm.effects.testIn(backgroundScope)

        vm.onEvent(LoginEvent.ErrorDialogRetryClicked)

        assertThat(effects.awaitItem()).isEqualTo(LoginEffect.SignedIn(isNewUser = false))
        assertThat(vm.state.value).isEqualTo(LoginUiState())
        assertThat(repository.idTokens).isEqualTo(listOf("id-token", "id-token"))
        effects.cancel()
    }

    private class FakeGoogle : GoogleIdTokenProvider {
        var result: GoogleSignInResult = GoogleSignInResult.Cancelled
        override suspend fun requestIdToken(): GoogleSignInResult {
            delay(10)   // real providers suspend; lets every intermediate state be observed
            return result
        }
    }

    private class FakeAuthRepository : AuthRepository {
        var result: AppResult<SignInOutcome> = AppResult.Failure(AppError.Unknown(null))
        val idTokens = mutableListOf<String>()
        override suspend fun signInWithGoogle(idToken: String): AppResult<SignInOutcome> {
            idTokens += idToken
            delay(10)
            return result
        }
    }
}
