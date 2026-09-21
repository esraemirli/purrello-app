package com.purrello.core.data.storage

import android.content.Context
import android.content.SharedPreferences

class AndroidKeyValueStore(context: Context, fileName: String = "purrello_prefs") : KeyValueStore {
    private val prefs: SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    override fun getString(key: String): String? = prefs.getString(key, null)
    override fun putString(key: String, value: String) = prefs.edit().putString(key, value).apply()
    override fun remove(key: String) = prefs.edit().remove(key).apply()
}

/** TODO(security): replace with Android Keystore-backed encryption before release. Private-mode prefs for now. */
class AndroidSecureStore(context: Context) : SecureStore {
    private val delegate = AndroidKeyValueStore(context, fileName = "purrello_secure")

    override fun getString(key: String): String? = delegate.getString(key)
    override fun putString(key: String, value: String) = delegate.putString(key, value)
    override fun remove(key: String) = delegate.remove(key)
}
