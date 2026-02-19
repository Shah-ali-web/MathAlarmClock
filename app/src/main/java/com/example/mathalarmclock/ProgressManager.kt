package com.example.mathalarmclock

import android.content.Context
import java.util.Calendar

object ProgressManager {

    private const val PREF_NAME = "weekly_progress"

    private fun getDayKey(): String {
        val cal = Calendar.getInstance()
        return "day_${cal.get(Calendar.DAY_OF_WEEK)}"
    }

    fun addProgress(context: Context, value: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val key = getDayKey()
        val current = prefs.getInt(key, 0)
        prefs.edit().putInt(key, (current + value).coerceAtMost(100)).apply()
    }

    // 🔥 Added missing function
    fun saveDailyProgress(context: Context, progress: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(getDayKey(), progress).apply()
    }

    fun resetToday(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(getDayKey(), 0).apply()
    }

    fun getWeeklyProgress(context: Context): List<Int> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return (1..7).map {
            prefs.getInt("day_$it", 0)
        }
    }
}
