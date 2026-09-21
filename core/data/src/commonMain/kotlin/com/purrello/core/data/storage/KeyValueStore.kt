package com.purrello.core.data.storage

/**
 * Small, non-sensitive preferences (selected pet, dismissed coach marks).
 * Android: SharedPreferences, iOS: NSUserDefaults — bound by the platform Koin module.
 */
interface KeyValueStore {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
}

/**
 * Secrets (auth tokens). Same shape as [KeyValueStore] but implementations MUST be encrypted at rest.
 * TODO(security): back with Android Keystore-encrypted storage and iOS Keychain before the first release.
 */
interface SecureStore {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
}

class InMemoryKeyValueStore : KeyValueStore, SecureStore {
    private val values = mutableMapOf<String, String>()
    override fun getString(key: String): String? = values[key]
    override fun putString(key: String, value: String) { values[key] = value }
    override fun remove(key: String) { values.remove(key) }
}
