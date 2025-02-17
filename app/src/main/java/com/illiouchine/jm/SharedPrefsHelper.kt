package com.illiouchine.jm

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsHelper(
    context: Context
){
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(ONBOARDING_PREF_KEY, Context.MODE_PRIVATE)

    fun getShowOnboarding(): Boolean {
        return sharedPreferences.getBoolean(SHOW_ONBOARDING_PREF_KEY, true)
    }

    fun editShowOnboarding(value: Boolean = false){
        sharedPreferences.edit()
            .putBoolean(SHOW_ONBOARDING_PREF_KEY, value)
            .apply()
    }

    companion object {
        private const val ONBOARDING_PREF_KEY: String = "onboarding_prefs"
        private const val SHOW_ONBOARDING_PREF_KEY: String = "show_onBoarding"
    }
}