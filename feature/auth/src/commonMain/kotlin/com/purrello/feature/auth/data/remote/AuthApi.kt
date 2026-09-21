package com.purrello.feature.auth.data.remote

import com.purrello.core.common.AppResult
import com.purrello.core.network.safeApiCall
import com.purrello.feature.auth.data.remote.dto.AuthResponse
import com.purrello.feature.auth.data.remote.dto.GoogleSignInRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal interface AuthApi {
    suspend fun signInWithGoogle(request: GoogleSignInRequest): AppResult<AuthResponse>
}

internal class KtorAuthApi(private val client: HttpClient) : AuthApi {
    override suspend fun signInWithGoogle(request: GoogleSignInRequest): AppResult<AuthResponse> =
        safeApiCall { client.post("v1/auth/google") { setBody(request) } }
}
