package com.purrello.feature.auth

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.purrello.core.network.PurrelloJson
import com.purrello.feature.auth.data.fake.AuthFixtures
import com.purrello.feature.auth.data.remote.dto.AuthResponse
import com.purrello.feature.auth.data.remote.dto.UserDto
import kotlin.test.Test

/** Contract test — fixture is docs/api/common/auth.md §2.1 verbatim. */
class AuthResponseDecodingTest {

    @Test
    fun `GIVEN contract example WHEN decoded THEN matches the documented fields`() {
        val decoded = PurrelloJson.decodeFromString(AuthResponse.serializer(), AuthFixtures.SIGN_IN_RESPONSE)

        assertThat(decoded).isEqualTo(
            AuthResponse(
                accessToken = "access-token-123",
                refreshToken = "refresh-token-456",
                expiresInSeconds = 3600,
                user = UserDto(
                    id = "usr_01J8Z6",
                    displayName = "Esra Emirli",
                    email = "esra@example.com",
                    avatarUrl = null,
                    isNewUser = true,
                ),
            ),
        )
    }

    @Test
    fun `GIVEN optional user fields missing WHEN decoded THEN defaults apply`() {
        val json = """{"accessToken":"a","refreshToken":"r","expiresInSeconds":60,"user":{"id":"u1"}}"""

        val decoded = PurrelloJson.decodeFromString(AuthResponse.serializer(), json)

        assertThat(decoded.user).isEqualTo(UserDto(id = "u1"))
    }
}
