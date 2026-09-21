package com.purrello.core.network

import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

internal actual fun currentAcceptLanguage(): String =
    NSLocale.preferredLanguages.firstOrNull() as? String ?: "tr-TR"
