package com.purrello.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

// Mirrors docs/api/common/auth.md §2.1 exactly.

@Serializable
internal data class GoogleSignInRequest(val idToken: String)

@Serializable
internal data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long,
    val user: UserDto,
)

@Serializable
internal data class UserDto(
    val id: String,
    val displayName: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val isNewUser: Boolean = false,
)
