package com.purrello.core.network

import com.purrello.core.common.AppConfig
import com.purrello.core.network.auth.AuthTokens
import com.purrello.core.network.auth.RefreshTokenRequest
import com.purrello.core.network.auth.RefreshTokenResponse
import com.purrello.core.network.auth.SessionEvents
import com.purrello.core.network.auth.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProviders
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal const val REFRESH_PATH = "v1/auth/refresh"

/**
 * The single HttpClient. Engine comes from the platform classpath (OkHttp on Android, Darwin on iOS).
 * Headers on every call: api-contract.md §7.
 */
@OptIn(ExperimentalUuidApi::class)
fun createHttpClient(
    config: AppConfig,
    tokenStorage: TokenStorage,
    sessionEvents: SessionEvents,
): HttpClient = HttpClient {
    expectSuccess = false   // safeApiCall maps non-2xx itself

    install(ContentNegotiation) { json(PurrelloJson) }

    install(HttpTimeout) {
        connectTimeoutMillis = 15_000
        requestTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
    }

    install(Auth) {
        bearer {
            loadTokens {
                tokenStorage.get()?.let { BearerTokens(it.accessToken, it.refreshToken) }
            }
            refreshTokens {
                val refreshToken = oldTokens?.refreshToken
                if (refreshToken == null) {
                    sessionEvents.notifyExpired()
                    return@refreshTokens null
                }
                val response = client.post(REFRESH_PATH) {
                    markAsRefreshTokenRequest()
                    contentType(ContentType.Application.Json)
                    setBody(RefreshTokenRequest(refreshToken))
                }
                if (response.status.isSuccess()) {
                    val body = response.body<RefreshTokenResponse>()
                    tokenStorage.save(AuthTokens(body.accessToken, body.refreshToken))
                    BearerTokens(body.accessToken, body.refreshToken)
                } else {
                    tokenStorage.clear()
                    sessionEvents.notifyExpired()
                    null
                }
            }
            // Auth endpoints (google sign-in, refresh) are called without a bearer token.
            sendWithoutRequest { request -> !request.url.encodedPath.contains("/auth/") }
        }
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level = if (config.isDebug) LogLevel.HEADERS else LogLevel.NONE
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }

    defaultRequest {
        url(config.baseUrl)
        contentType(ContentType.Application.Json)
        header(HttpHeaders.AcceptLanguage, currentAcceptLanguage())
        header("X-App-Version", config.appVersion)
        header("X-Platform", config.platform.headerValue)
        header("X-Request-Id", Uuid.random().toString())
    }
}

/** Drop cached bearer tokens after login/logout so the next request reloads them from [TokenStorage]. */
fun HttpClient.clearCachedTokens() {
    authProviders.filterIsInstance<BearerAuthProvider>().forEach { it.clearToken() }
}
