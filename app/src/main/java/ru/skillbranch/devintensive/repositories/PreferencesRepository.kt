package ru.skillbranch.devintensive.repositories

import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.models.Profile

object PreferencesRepository {

    private const val APP_THEME = "APP_THEME"
    private const val FIRST_NAME = "FIRST_NAME"
    private const val LAST_NAME = "LAST_NAME"
    private const val ABOUT = "ABOUT"
    private const val REPOSITORY = "REPOSITORY"
    private const val RATING = "RATING"
    private const val RESPECT = "RESPECT"

    private val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(App.appContext) }

    fun saveAppTheme(theme: Int) {
        putValue(APP_THEME, theme)
    }

    fun getAppTheme() = prefs.getInt(APP_THEME, AppCompatDelegate.MODE_NIGHT_NO)

    fun getProfile() = Profile(getValue(FIRST_NAME, ""),
            getValue(LAST_NAME, ""),
            getValue(ABOUT, ""),
            getValue(REPOSITORY, ""),
            getValue(RATING, 0),
            getValue(RESPECT, 0))

    fun saveProfile(profile: Profile) = with(profile) {
        putValue(FIRST_NAME, firstName)
        putValue(LAST_NAME, lastName)
        putValue(ABOUT, about)
        putValue(REPOSITORY, repository)
        putValue(RATING, rating)
        putValue(RESPECT, respect)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <U> getValue(name: String, default: U): U = when (default) {
        is Long -> prefs.getLong(name, default) as U
        is String -> prefs.getString(name, default) as? U
        is Int -> prefs.getInt(name, default) as U
        is Boolean -> prefs.getBoolean(name, default) as U
        is Float -> prefs.getFloat(name, default) as U
        else -> error("Only primitive types can be stored in SharedPreferences")
    } ?: default

    private fun putValue(key: String, value: Any) = with(prefs.edit()) {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            else -> error("Only primitive types can be stored in SharedPreferences")
        }
    }.apply()
}