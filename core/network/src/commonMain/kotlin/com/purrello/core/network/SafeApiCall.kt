package com.purrello.core.network

import com.purrello.core.common.AppError
import com.purrello.core.common.AppResult
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

/**
 * The ONLY place in the app that catches exceptions from network I/O (architecture.md §5).
 * Wrap every API call: `safeApiCall<HomeScreenResponse> { client.get("v1/screens/home") }`.
 * Never swallows [CancellationException].
 */
suspend inline fun <reified T> safeApiCall(crossinline request: suspend () -> HttpResponse): AppResult<T> {
    val response = try {
        request()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        return AppResult.Failure(e.toTransportError())
    }

    if (!response.status.isSuccess()) return AppResult.Failure(response.toAppError())

    return try {
        AppResult.Success(response.body<T>())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        AppResult.Failure(AppError.Unknown(e))
    }
}

/** Failure before any response arrived. */
@PublishedApi
internal fun Throwable.toTransportError(): AppError = when (this) {
    is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> AppError.Timeout
    is SerializationException -> AppError.Unknown(this)     // request body could not be encoded (client bug)
    else -> AppError.NoConnection
}

/** Non-2xx response → typed error, using the contract error body when present. */
@PublishedApi
internal suspend fun HttpResponse.toAppError(): AppError {
    val body: ErrorBody? = try {
        PurrelloJson.decodeFromString(ErrorResponse.serializer(), bodyAsText()).error
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }
    return when (status.value) {
        401 -> AppError.Unauthorized
        422 -> AppError.Validation(
            fields = body?.fieldErrors.orEmpty().associate { it.field to it.code }.toImmutableMap(),
            traceId = body?.traceId,
        )
        else -> AppError.Server(status = status.value, code = body?.code, traceId = body?.traceId)
    }
}
