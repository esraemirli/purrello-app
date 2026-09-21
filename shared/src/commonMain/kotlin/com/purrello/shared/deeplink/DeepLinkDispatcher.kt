package com.purrello.shared.deeplink

import com.purrello.core.navigation.DeepLink
import com.purrello.core.navigation.DeepLinkParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Push / link payloads arrive from the platform (FCM on Android, Swift AppDelegate on iOS) at any time,
 * possibly before login. The link stays pending until the app is in Main, then App() applies it.
 */
class DeepLinkDispatcher {
    private val _pending = MutableStateFlow<DeepLink?>(null)
    val pending: StateFlow<DeepLink?> = _pending.asStateFlow()

    fun dispatch(payload: Map<String, String>) {
        DeepLinkParser.parse(payload)?.let { _pending.value = it }
    }

    fun consume() {
        _pending.value = null
    }
}
