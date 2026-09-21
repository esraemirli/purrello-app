package com.purrello.core.network

import kotlinx.serialization.json.Json

/** The only Json configuration for network payloads (api-contract.md §5, kmp-conventions.md §9). */
val PurrelloJson: Json = Json {
    ignoreUnknownKeys = true      // additive backend changes never break old clients
    explicitNulls = false         // missing ≡ null
    coerceInputValues = true      // unknown enum value → property default (UNKNOWN)
    encodeDefaults = true
}
