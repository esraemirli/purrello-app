package com.purrello.core.network

import kotlinx.serialization.Serializable

/** Error body contract — api-contract.md §6. */
@Serializable
internal data class ErrorResponse(val error: ErrorBody? = null)

@Serializable
internal data class ErrorBody(
    val code: String? = null,
    val message: String? = null,          // logs only — never shown to users
    val traceId: String? = null,
    val fieldErrors: List<FieldErrorDto> = emptyList(),
)

@Serializable
internal data class FieldErrorDto(val field: String, val code: String)
