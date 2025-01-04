package com.official.taqreebpro

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val COMPANY_NAME = "company_name"
    }

    // Set the logged-in state
    fun setLoginState(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply()
    }

    // Get the logged-in state
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    // Save the company name
    fun saveCompanyName(companyName: String) {
        prefs.edit().putString(COMPANY_NAME, companyName).apply()
    }

    // Get the company name
    fun getCompanyName(): String? {
        return prefs.getString(COMPANY_NAME, null)
    }

    // Clear session (useful for logout functionality)
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
