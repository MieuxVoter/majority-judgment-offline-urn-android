package com.illiouchine.jm.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.logic.DEFAULT_PLAY_SOUND_VALUE
import com.illiouchine.jm.logic.DEFAULT_SHOW_ONBOARDING_VALUE
import com.illiouchine.jm.model.Grading

class SharedPrefsHelper(
    context: Context,
) {
    companion object {
        private const val SETTINGS_PREF_KEY: String = "settings_prefs"
        private const val SHOW_ONBOARDING_PREF_KEY: String = "show_onBoarding"
        private const val PLAY_SOUND_PREF_KEY: String = "play_sound"
        private const val PIN_SCREEN_PREF_KEY: String = "pin_screen"
        private const val DEFAULT_GRADING_PREF_KEY: String = "default_grading_prefs"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SETTINGS_PREF_KEY, Context.MODE_PRIVATE)

    fun getShowOnboarding(): Boolean {
        return sharedPreferences.getBoolean(
            SHOW_ONBOARDING_PREF_KEY,
            DEFAULT_SHOW_ONBOARDING_VALUE,
        )
    }

    fun editShowOnboarding(value: Boolean = false) {
        sharedPreferences.edit()
            .putBoolean(SHOW_ONBOARDING_PREF_KEY, value)
            .apply()
    }

    fun getPlaySound(): Boolean {
        return sharedPreferences.getBoolean(
            PLAY_SOUND_PREF_KEY,
            DEFAULT_PLAY_SOUND_VALUE,
        )
    }

    fun editPlaySound(value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(PLAY_SOUND_PREF_KEY, value)
            .apply()
    }

    fun getPinScreen(): Boolean {
        return sharedPreferences.getBoolean(PIN_SCREEN_PREF_KEY, false)
    }

    fun editPinScreen(value: Boolean = true) {
        sharedPreferences.edit()
            .putBoolean(PIN_SCREEN_PREF_KEY, value)
            .apply()
    }

    fun getDefaultGrading(): Grading {
        val defaultAmountOfGrading = sharedPreferences.getInt(
            DEFAULT_GRADING_PREF_KEY,
            DEFAULT_GRADING_QUALITY_VALUE.getAmountOfGrades(),
        )
        return Grading.byAmountOfGrades(defaultAmountOfGrading)
    }

    fun editDefaultGrading(grading: Grading) {
        sharedPreferences.edit()
            .putInt(DEFAULT_GRADING_PREF_KEY, grading.getAmountOfGrades())
            .apply()
    }
}