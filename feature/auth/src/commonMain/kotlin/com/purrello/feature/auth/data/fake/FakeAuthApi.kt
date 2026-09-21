package com.purrello.feature.auth.data.fake

import com.purrello.core.common.AppError
import com.purrello.core.common.AppResult
import com.purrello.core.network.PurrelloJson
import com.purrello.feature.auth.data.remote.AuthApi
import com.purrello.feature.auth.data.remote.dto.AuthResponse
import com.purrello.feature.auth.data.remote.dto.GoogleSignInRequest
import com.purrello.feature.auth.domain.GoogleIdTokenProvider
import com.purrello.feature.auth.domain.GoogleSignInResult
import kotlinx.coroutines.delay

/** Contract example JSON — copied verbatim from docs/api/common/auth.md §2.1. */
internal object AuthFixtures {
    const val SIGN_IN_RESPONSE = """
{
  "accessToken": "access-token-123",
  "refreshToken": "refresh-token-456",
  "expiresInSeconds": 3600,
  "user": {
    "id": "usr_01J8Z6",
    "displayName": "Esra Emirli",
    "email": "esra@example.com",
    "avatarUrl": null,
    "isNewUser": true
  }
}
"""
}

/** Used while `AppConfig.useFakeApi` is true. Set [nextError] to exercise error surfaces manually. */
internal class FakeAuthApi : AuthApi {
    var nextError: AppError? = null

    override suspend fun signInWithGoogle(request: GoogleSignInRequest): AppResult<AuthResponse> {
        delay(FAKE_LATENCY_MS)
        nextError?.let { return AppResult.Failure(it) }
        return AppResult.Success(PurrelloJson.decodeFromString(AuthResponse.serializer(), AuthFixtures.SIGN_IN_RESPONSE))
    }
}

internal class FakeGoogleIdTokenProvider : GoogleIdTokenProvider {
    override suspend fun requestIdToken(): GoogleSignInResult {
        delay(FAKE_LATENCY_MS)
        return GoogleSignInResult.Success(idToken = "fake-google-id-token")
    }
}

private const val FAKE_LATENCY_MS = 400L
