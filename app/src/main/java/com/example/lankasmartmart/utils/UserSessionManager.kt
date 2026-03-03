package com.example.lankasmartmart.utils

import android.content.Context
import android.content.SharedPreferences
<<<<<<< HEAD
import androidx.core.content.edit
=======
>>>>>>> 91ad457926b7ee01249bb02b9830ce902b91e9c8
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
<<<<<<< HEAD
        prefs.edit {putString(KEY_USER_DATA, json)}
=======
        prefs.edit().putString(KEY_USER_DATA, json).apply()
>>>>>>> 91ad457926b7ee01249bb02b9830ce902b91e9c8
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
<<<<<<< HEAD
        prefs.edit {remove(KEY_USER_DATA)}
=======
        prefs.edit().remove(KEY_USER_DATA).apply()
>>>>>>> 91ad457926b7ee01249bb02b9830ce902b91e9c8
    }

    /**
     * Checks if a user session exists.
     */
    fun isLoggedIn(): Boolean {
        return getUserSession() != null
    }
}
