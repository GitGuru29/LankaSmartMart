package com.example.lankasmartmart.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.lankasmartmart.viewmodel.UserData
import com.google.gson.Gson

/**
 * Manages user session persistence using SharedPreferences.
 */
class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "LankaSmartMartSession"
        private const val KEY_USER_DATA = "user_data"
    }

    /**
     * Saves the current user data to local storage.
     */
    fun saveUserSession(userData: UserData) {
        val json = gson.toJson(userData)
        prefs.edit().putString(KEY_USER_DATA, json).apply()
    }

    /**
     * Retrieves the saved user data from local storage.
     */
    fun getUserSession(): UserData? {
        val json = prefs.getString(KEY_USER_DATA, null) ?: return null
        return try {
            gson.fromJson(json, UserData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Clears the saved user data from local storage.
     */
    fun clearSession() {
        prefs.edit().remove(KEY_USER_DATA).apply()
    }

    /**
     * Checks if a user session exists.
     */
    fun isLoggedIn(): Boolean {
        return getUserSession() != null
    }
}
