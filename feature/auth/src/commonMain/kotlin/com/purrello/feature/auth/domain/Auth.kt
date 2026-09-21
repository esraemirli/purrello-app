package com.purrello.feature.auth.domain

import com.purrello.core.common.AppError
import com.purrello.core.common.AppResult

interface AuthRepository {
    /** Exchanges a Google ID token for Purrello tokens and starts the session. */
    suspend fun signInWithGoogle(idToken: String): AppResult<SignInOutcome>
}

data class SignInOutcome(val isNewUser: Boolean)

/**
 * Obtains a Google ID token on the device. Platform implementations (Credential Manager on Android,
 * GoogleSignIn SDK on iOS via a Swift bridge) are bound by the platform Koin module.
 */
interface GoogleIdTokenProvider {
    suspend fun requestIdToken(): GoogleSignInResult
}

sealed interface GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult
    /** User closed the Google sheet — show nothing (product rule). */
    data object Cancelled : GoogleSignInResult
    data class Failed(val error: AppError) : GoogleSignInResult
}

/** Bound until the platform bridge exists, so a real-backend build fails loudly instead of silently. */
internal class NotConfiguredGoogleIdTokenProvider : GoogleIdTokenProvider {
    override suspend fun requestIdToken(): GoogleSignInResult =
        GoogleSignInResult.Failed(AppError.Unknown(IllegalStateException("GoogleIdTokenProvider not configured for this platform")))
}
