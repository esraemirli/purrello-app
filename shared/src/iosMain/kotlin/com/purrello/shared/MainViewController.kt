package com.purrello.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.purrello.core.common.AppConfig
import com.purrello.core.data.storage.IosKeyValueStore
import com.purrello.core.data.storage.IosSecureStore
import com.purrello.core.data.storage.KeyValueStore
import com.purrello.core.data.storage.SecureStore
import com.purrello.shared.di.handleDeepLinkPayload
import com.purrello.shared.di.initKoin
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }

/**
 * Called once from Swift (AppDelegate) before the UI is created.
 * Not named `init…` — Objective-C reserves that prefix.
 */
fun startPurrelloIos(environment: String, baseUrl: String, useFakeApi: Boolean, isDebug: Boolean, appVersion: String) {
    initKoin(
        config = AppConfig(
            environment = AppConfig.Environment.entries.firstOrNull { it.name == environment } ?: AppConfig.Environment.DEV,
            baseUrl = baseUrl,
            useFakeApi = useFakeApi,
            isDebug = isDebug,
            appVersion = appVersion,
            platform = AppConfig.Platform.IOS,
        ),
        platformModule = module {
            single<KeyValueStore> { IosKeyValueStore() }
            single<SecureStore> { IosSecureStore() }
            // TODO(platform): GoogleIdTokenProvider bridged from Swift (GoogleSignIn SDK).
        },
    )
}

/** Swift → Kotlin: user opened a notification. `userInfo` values are stringified. */
fun onPushNotificationOpened(userInfo: Map<Any?, *>) {
    handleDeepLinkPayload(
        userInfo.entries
            .mapNotNull { (key, value) -> (key as? String)?.let { it to value.toString() } }
            .toMap(),
    )
}
