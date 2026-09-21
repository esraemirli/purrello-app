package com.purrello.core.data.storage

import platform.Foundation.NSUserDefaults

class IosKeyValueStore(private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults) : KeyValueStore {
    override fun getString(key: String): String? = defaults.stringForKey(key)
    override fun putString(key: String, value: String) = defaults.setObject(value, forKey = key)
    override fun remove(key: String) = defaults.removeObjectForKey(key)
}

/** TODO(security): replace with Keychain (kSecClassGenericPassword) before release. */
class IosSecureStore : SecureStore {
    private val delegate = IosKeyValueStore(NSUserDefaults(suiteName = "purrello.secure"))

    override fun getString(key: String): String? = delegate.getString(key)
    override fun putString(key: String, value: String) = delegate.putString(key, value)
    override fun remove(key: String) = delegate.remove(key)
}
