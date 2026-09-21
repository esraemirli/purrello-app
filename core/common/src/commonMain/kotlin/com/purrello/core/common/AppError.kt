package com.purrello.core.common

import kotlinx.collections.immutable.ImmutableMap

/**
 * Every failure the app can show. Produced by `safeApiCall` (core:network) and platform bridges only.
 * Surfaces per type: architecture.md §5 / ui-conventions.md §3.
 */
sealed interface AppError {
    /** No response at all: offline, DNS, TLS, connection reset. */
    data object NoConnection : AppError

    data object Timeout : AppError

    /** Session could not be refreshed. Handled globally (back to Login); screens don't render it. */
    data object Unauthorized : AppError

    /** Non-2xx response. [code] is the backend error code (api-contract.md §6); never show `message`. */
    data class Server(val status: Int, val code: String?, val traceId: String?) : AppError

    /** 422 — field name (as in the request) → error code. Shown inline under each field. */
    data class Validation(val fields: ImmutableMap<String, String>, val traceId: String?) : AppError

    data class Unknown(val cause: Throwable?) : AppError
}
