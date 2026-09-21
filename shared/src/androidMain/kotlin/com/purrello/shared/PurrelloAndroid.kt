package com.purrello.shared

import android.content.Context
import com.purrello.core.common.AppConfig
import com.purrello.core.data.storage.AndroidKeyValueStore
import com.purrello.core.data.storage.AndroidSecureStore
import com.purrello.core.data.storage.KeyValueStore
import com.purrello.core.data.storage.SecureStore
import com.purrello.shared.di.initKoin
import org.koin.dsl.module

/** Call from Application.onCreate. */
fun startPurrelloAndroid(context: Context, config: AppConfig) {
    val appContext = context.applicationContext
    initKoin(
        config = config,
        platformModule = module {
            single<KeyValueStore> { AndroidKeyValueStore(appContext) }
            single<SecureStore> { AndroidSecureStore(appContext) }
            // TODO(platform): single<GoogleIdTokenProvider> { CredentialManagerGoogleIdTokenProvider(...) }
        },
    )
}
