package com.purrello.core.common

/**
 * Build/environment configuration, created by the platform entry point (Android BuildConfig,
 * iOS values passed from Swift) and provided through Koin. Shared code never reads BuildConfig.
 */
data class AppConfig(
    val environment: Environment,
    val baseUrl: String,
    /** true → feature APIs are bound to their `Fake…Api` (contract example JSON). */
    val useFakeApi: Boolean,
    val isDebug: Boolean,
    val appVersion: String,
    val platform: Platform,
) {
    enum class Environment { DEV, STAGING, PROD }
    enum class Platform(val headerValue: String) { ANDROID("android"), IOS("ios") }
}
