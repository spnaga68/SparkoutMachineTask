package com.imtamila.sparkoutmachinetask.utils

import android.content.Context

private const val PREFERENCE_NAME = "sparkoutTaskPreference"

object SessionManager {

    fun saveSession(context: Context, key: String, value: String = "") {
        val sharedPref =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun getSession(context: Context, key: String, defValue: String = ""): String {
        val sharedPref =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE) ?: return defValue
        return sharedPref.getString(key, defValue) ?: defValue
    }
}
