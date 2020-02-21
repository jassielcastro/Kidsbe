package com.ajcm.data.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class LocalDB(context: Context) {

    private val prefName: String by lazy {
        "com.ajcm.kidstube.prefs"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(prefName, MODE_PRIVATE)
    }

    private fun setStringPrefs(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    private fun getStringPrefs(key: String): String? = prefs.getString(key, "")

    fun clear() {
        prefs.edit().clear().apply()
    }

    var accountName: String
        get() = getStringPrefs("accountName") ?: ""
        set(value) = setStringPrefs("accountName", value)
}