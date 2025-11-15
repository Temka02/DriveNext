package com.example.drivenext.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("DriveNextPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_SURNAME = "user_surname"
        private const val KEY_REGISTRATION_DATE = "registration_date"
    }

    fun createLoginSession(userId: Int, email: String, name: String, surname: String, registrationDate: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_USER_SURNAME, surname)
        editor.putString(KEY_REGISTRATION_DATE, registrationDate)
        editor.apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUserEmail(): String = prefs.getString(KEY_USER_EMAIL, "") ?: ""
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""
    fun getUserSurname(): String = prefs.getString(KEY_USER_SURNAME, "") ?: ""
    fun getRegistrationDate(): String = prefs.getString(KEY_REGISTRATION_DATE, "") ?: ""

    fun logout() {
        editor.clear()
        editor.apply()
    }
}