package com.example.mathalarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskCleanupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPref = context.getSharedPreferences("TASKS", Context.MODE_PRIVATE)
        sharedPref.edit().remove("today_tasks").apply()
    }
}
