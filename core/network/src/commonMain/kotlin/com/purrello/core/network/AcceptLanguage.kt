package com.purrello.core.network

/** Device/app language as a BCP-47 tag list for `Accept-Language` (catalog names are localized by the backend). */
internal expect fun currentAcceptLanguage(): String
