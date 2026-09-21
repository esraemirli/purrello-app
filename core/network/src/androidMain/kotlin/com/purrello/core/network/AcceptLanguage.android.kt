package com.purrello.core.network

import java.util.Locale

internal actual fun currentAcceptLanguage(): String = Locale.getDefault().toLanguageTag()
