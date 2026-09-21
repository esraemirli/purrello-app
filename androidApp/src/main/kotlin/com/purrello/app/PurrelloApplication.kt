package com.purrello.app

import android.app.Application
import com.purrello.core.common.AppConfig
import com.purrello.shared.startPurrelloAndroid

class PurrelloApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startPurrelloAndroid(
            context = this,
            config = AppConfig(
                environment = AppConfig.Environment.valueOf(BuildConfig.ENVIRONMENT),
                baseUrl = BuildConfig.BASE_URL,
                useFakeApi = BuildConfig.USE_FAKE_API,
                isDebug = BuildConfig.DEBUG,
                appVersion = BuildConfig.VERSION_NAME,
                platform = AppConfig.Platform.ANDROID,
            ),
        )
    }
}
