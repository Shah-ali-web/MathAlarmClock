package com.example.mathalarmclock

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object StreakManager {

    private const val PREF_NAME = "StreakPrefs"
    private const val KEY_STREAK = "current_streak"
    private const val KEY_LAST_DATE = "last_completed_date"

    fun updateStreak(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val today = getTodayDate()
        val lastDate = prefs.getString(KEY_LAST_DATE, null)
        var streak = prefs.getInt(KEY_STREAK, 0)

        streak = when {
            lastDate == null -> 1
            lastDate == today -> streak
            isYesterday(lastDate) -> streak + 1
            else -> 1
        }

        prefs.edit()
            .putInt(KEY_STREAK, streak)
            .putString(KEY_LAST_DATE, today)
            .apply()
    }

    fun getStreak(context: Context): Int {
        return context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_STREAK, 0)
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())
    }

    private fun isYesterday(date: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val last = sdf.parse(date) ?: return false

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)

        return sdf.format(cal.time) == sdf.format(last)
    }
}
