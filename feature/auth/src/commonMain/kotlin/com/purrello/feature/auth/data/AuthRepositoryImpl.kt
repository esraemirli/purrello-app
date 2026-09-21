package com.purrello.feature.auth.data

import com.purrello.core.common.AppResult
import com.purrello.core.common.map
import com.purrello.core.data.session.SessionRepository
import com.purrello.core.network.auth.AuthTokens
import com.purrello.feature.auth.data.remote.AuthApi
import com.purrello.feature.auth.data.remote.dto.GoogleSignInRequest
import com.purrello.feature.auth.domain.AuthRepository
import com.purrello.feature.auth.domain.SignInOutcome

internal class AuthRepositoryImpl(
    private val api: AuthApi,
    private val session: SessionRepository,
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): AppResult<SignInOutcome> {
        val result = api.signInWithGoogle(GoogleSignInRequest(idToken))
        if (result is AppResult.Success) {
            session.startSession(AuthTokens(result.value.accessToken, result.value.refreshToken))
        }
        return result.map { SignInOutcome(isNewUser = it.user.isNewUser) }
    }
}
