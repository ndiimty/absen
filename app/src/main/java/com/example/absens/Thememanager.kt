package com.example.absens

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * ThemeManager — Kelola dark/light theme di seluruh aplikasi.
 *
 * Cara pakai:
 *   1. Di Application.onCreate() → ThemeManager.applyTheme(this)
 *   2. Toggle tema → ThemeManager.toggle(this)
 *   3. Cek status  → ThemeManager.isDarkMode(this)
 */
object ThemeManager {

    private const val PREFS_NAME  = "app_theme_prefs"
    private const val KEY_IS_DARK = "is_dark_mode"

    // ─────────────────────────────────────────────
    // Terapkan tema yang tersimpan (panggil di App.onCreate)
    // ─────────────────────────────────────────────
    fun applyTheme(context: Context) {
        val mode = if (isDarkMode(context)) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    // ─────────────────────────────────────────────
    // Toggle tema (panggil dari tombol/switch)
    // ─────────────────────────────────────────────
    fun toggle(context: Context) {
        val newIsDark = !isDarkMode(context)
        prefs(context).edit().putBoolean(KEY_IS_DARK, newIsDark).apply()

        val mode = if (newIsDark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    // ─────────────────────────────────────────────
    // Set tema secara eksplisit (true = gelap)
    // ─────────────────────────────────────────────
    fun setDark(context: Context, dark: Boolean) {
        prefs(context).edit().putBoolean(KEY_IS_DARK, dark).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    // ─────────────────────────────────────────────
    // Cek apakah dark mode aktif
    // ─────────────────────────────────────────────
    fun isDarkMode(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_IS_DARK, false) // default: light
    }

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}